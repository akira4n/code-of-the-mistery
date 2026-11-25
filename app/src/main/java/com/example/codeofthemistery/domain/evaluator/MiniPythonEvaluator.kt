package com.example.codeofthemistery.domain.evaluator

import com.example.codeofthemistery.domain.model.CodeValidationRule
import kotlin.math.abs

/**
 * Lightweight, sandboxed Python-subset interpreter and validator written in pure Kotlin.
 * Supports variables, basic data types, arithmetic/comparison/logical ops, if/elif/else,
 * for-range loops, while loops (guarded by [MAX_STEPS]), simple functions, and print().
 */
class MiniPythonEvaluator {

    companion object {
        const val MAX_STEPS = 200
    }

    private class EvalException(message: String) : RuntimeException(message)
    private class ReturnSignal(val value: Any?) : RuntimeException()

    private data class SourceLine(val line: Int, val indent: Int, val content: String)

    private sealed interface Stmt {
        val line: Int
        data class Assign(override val line: Int, val name: String, val expr: String) : Stmt
        data class AugAssign(override val line: Int, val name: String, val op: String, val expr: String) : Stmt
        data class ExprOnly(override val line: Int, val expr: String) : Stmt
        data class Return(override val line: Int, val expr: String?) : Stmt
        data class IfBranch(val condition: String?, val body: List<Stmt>)
        data class IfBlock(override val line: Int, val branches: List<IfBranch>) : Stmt
        data class ForRange(override val line: Int, val varName: String, val iterableExpr: String, val body: List<Stmt>) : Stmt
        data class WhileLoop(override val line: Int, val condition: String, val body: List<Stmt>) : Stmt
        data class DefFunc(override val line: Int, val name: String, val params: List<String>, val body: List<Stmt>) : Stmt
    }

    fun evaluate(code: String, rule: CodeValidationRule? = null): ExecutionResult {
        val stdout = mutableListOf<String>()
        val globalScope = mutableMapOf<String, Any?>()
        val functions = mutableMapOf<String, Stmt.DefFunc>()
        var stepCount = 0

        fun tick(line: Int) {
            stepCount++
            if (stepCount > MAX_STEPS) {
                throw EvalException("RuntimeError di baris $line: Batas eksekusi ($MAX_STEPS langkah) terlampaui — periksa potensi infinite loop.")
            }
        }

        return try {
            val lines = preprocessLines(code)
            if (rule?.rejectUnfilledPlaceholders != false && lines.any { it.content.contains("____") }) {
                throw EvalException("SyntaxError: Masih ada slot '____' yang belum diisi! Ketuk Code Chips atau ketik nilainya.")
            }
            val (stmts, _) = parseBlock(lines, 0, baseIndent = 0)
            executeBlock(stmts, globalScope, functions, stdout, ::tick)

            val ruleError = if (rule != null) validateRule(code, globalScope, stdout, rule) else null
            if (ruleError != null) {
                ExecutionResult(
                    isSuccess = true,
                    isRulePassed = false,
                    exitCode = 1,
                    stdoutLines = stdout,
                    variables = globalScope.toMap(),
                    errorMessage = ruleError
                )
            } else {
                ExecutionResult(
                    isSuccess = true,
                    isRulePassed = true,
                    exitCode = 0,
                    stdoutLines = stdout,
                    variables = globalScope.toMap(),
                    errorMessage = null
                )
            }
        } catch (e: EvalException) {
            ExecutionResult(
                isSuccess = false,
                isRulePassed = false,
                exitCode = 1,
                stdoutLines = stdout,
                variables = globalScope.toMap(),
                errorMessage = e.message ?: "SyntaxError"
            )
        } catch (e: Exception) {
            ExecutionResult(
                isSuccess = false,
                isRulePassed = false,
                exitCode = 1,
                stdoutLines = stdout,
                variables = globalScope.toMap(),
                errorMessage = "RuntimeError: ${e.message ?: "Eksekusi gagal"}"
            )
        }
    }

    private fun validateRule(
        code: String,
        vars: Map<String, Any?>,
        stdout: List<String>,
        rule: CodeValidationRule
    ): String? {
        val strippedCode = preprocessLines(code).joinToString("\n") { it.content }
        for (kw in rule.requiredKeywords) {
            val regex = Regex("\\b${Regex.escape(kw)}\\b")
            if (!regex.containsMatchIn(strippedCode)) {
                return "LogicError: Kode harus menggunakan kata kunci '$kw'."
            }
        }
        for (varName in rule.nonEmptyStringVars) {
            val v = vars[varName]
            if (v !is String || v.isBlank()) {
                return "LogicError: Variabel '$varName' harus diisi teks (string) yang tidak kosong."
            }
        }
        for ((key, expected) in rule.expectedVars) {
            if (!vars.containsKey(key)) {
                return "LogicError: Variabel '$key' belum didefinisikan."
            }
            val actual = vars[key]
            if (!valuesMatch(actual, expected)) {
                return "LogicError: Nilai variabel '$key' diharapkan ${formatPy(expected)}, tetapi hasilnya ${formatPy(actual)}."
            }
        }
        for ((key, allowedList) in rule.allowedVarValues) {
            if (!vars.containsKey(key)) {
                return "LogicError: Variabel '$key' belum didefinisikan."
            }
            val actual = vars[key]
            if (allowedList.none { valuesMatch(actual, it) }) {
                val expectedText = allowedList.joinToString(" atau ") { formatPy(it) }
                return "LogicError: Nilai variabel '$key' diharapkan $expectedText, tetapi hasilnya ${formatPy(actual)}."
            }
        }
        if (stdout.size < rule.minStdoutLines) {
            return "LogicError: Output print() minimal ${rule.minStdoutLines} baris, saat ini ${stdout.size} baris."
        }
        val fullOut = stdout.joinToString("\n")
        for (token in rule.expectedStdoutContains) {
            if (!fullOut.contains(token, ignoreCase = false)) {
                return "LogicError: Output terminal belum menampilkan '$token'."
            }
        }
        return null
    }

    private fun preprocessLines(code: String): List<SourceLine> {
        val result = mutableListOf<SourceLine>()
        code.replace("\r\n", "\n").split('\n').forEachIndexed { idx, raw ->
            val expanded = raw.replace("\t", "    ")
            val noComment = stripCommentOutsideQuotes(expanded)
            if (noComment.isBlank()) return@forEachIndexed
            val indent = noComment.indexOfFirst { !it.isWhitespace() }.let { if (it < 0) 0 else it }
            result += SourceLine(idx + 1, indent, noComment.trim())
        }
        return result
    }

    private fun stripCommentOutsideQuotes(line: String): String {
        var inSingle = false
        var inDouble = false
        var escaped = false
        for (i in line.indices) {
            val ch = line[i]
            if (escaped) {
                escaped = false
                continue
            }
            if (ch == '\\') {
                escaped = true
                continue
            }
            if (ch == '\'' && !inDouble) inSingle = !inSingle
            else if (ch == '"' && !inSingle) inDouble = !inDouble
            else if (ch == '#' && !inSingle && !inDouble) return line.substring(0, i)
        }
        return line
    }

    private fun parseBlock(lines: List<SourceLine>, startIndex: Int, baseIndent: Int): Pair<List<Stmt>, Int> {
        val stmts = mutableListOf<Stmt>()
        var i = startIndex
        while (i < lines.size) {
            val cur = lines[i]
            if (cur.indent < baseIndent) break
            if (cur.indent > baseIndent) {
                throw EvalException("IndentationError di baris ${cur.line}: indentasi tidak terduga.")
            }
            val text = cur.content
            when {
                text.startsWith("if ") || text.startsWith("if(") -> {
                    val (ifStmt, nextIdx) = parseIfChain(lines, i, baseIndent)
                    stmts += ifStmt
                    i = nextIdx
                }
                text.startsWith("elif ") || text.startsWith("else:") || text.startsWith("else :") -> {
                    throw EvalException("SyntaxError di baris ${cur.line}: '$text' tanpa blok 'if' sebelumnya.")
                }
                text.startsWith("for ") -> {
                    if (!text.endsWith(":")) throw EvalException("SyntaxError di baris ${cur.line}: tambahkan ':' di akhir baris 'for'.")
                    val header = text.removePrefix("for ").removeSuffix(":").trim()
                    val parts = header.split(Regex("\\s+in\\s+"), limit = 2)
                    if (parts.size != 2 || !isValidIdentifier(parts[0].trim())) {
                        throw EvalException("SyntaxError di baris ${cur.line}: format 'for <var> in <iterable>:' tidak valid.")
                    }
                    val (body, nextIdx) = parseIndentedChildBlock(lines, i + 1, baseIndent, cur.line)
                    stmts += Stmt.ForRange(cur.line, parts[0].trim(), parts[1].trim(), body)
                    i = nextIdx
                }
                text.startsWith("while ") -> {
                    if (!text.endsWith(":")) throw EvalException("SyntaxError di baris ${cur.line}: tambahkan ':' di akhir baris 'while'.")
                    val cond = text.removePrefix("while ").removeSuffix(":").trim()
                    val (body, nextIdx) = parseIndentedChildBlock(lines, i + 1, baseIndent, cur.line)
                    stmts += Stmt.WhileLoop(cur.line, cond, body)
                    i = nextIdx
                }
                text.startsWith("def ") -> {
                    if (!text.endsWith(":")) throw EvalException("SyntaxError di baris ${cur.line}: tambahkan ':' di akhir definisi 'def'.")
                    val sig = text.removePrefix("def ").removeSuffix(":").trim()
                    val openIdx = sig.indexOf('(')
                    val closeIdx = sig.lastIndexOf(')')
                    if (openIdx <= 0 || closeIdx < openIdx) {
                        throw EvalException("SyntaxError di baris ${cur.line}: format fungsi 'def nama(param):' tidak valid.")
                    }
                    val fnName = sig.substring(0, openIdx).trim()
                    val paramsRaw = sig.substring(openIdx + 1, closeIdx).trim()
                    val params = if (paramsRaw.isEmpty()) emptyList() else paramsRaw.split(',').map { it.trim() }
                    val (body, nextIdx) = parseIndentedChildBlock(lines, i + 1, baseIndent, cur.line)
                    stmts += Stmt.DefFunc(cur.line, fnName, params, body)
                    i = nextIdx
                }
                text == "return" || text.startsWith("return ") -> {
                    val expr = text.removePrefix("return").trim().ifEmpty { null }
                    stmts += Stmt.Return(cur.line, expr)
                    i++
                }
                else -> {
                    stmts += parseSimpleStmt(cur)
                    i++
                }
            }
        }
        return stmts to i
    }

    private fun parseIndentedChildBlock(
        lines: List<SourceLine>,
        nextIndex: Int,
        parentIndent: Int,
        headerLine: Int
    ): Pair<List<Stmt>, Int> {
        if (nextIndex >= lines.size || lines[nextIndex].indent <= parentIndent) {
            throw EvalException("IndentationError setelah baris $headerLine: blok kode di bawahnya harus diberi indentasi.")
        }
        return parseBlock(lines, nextIndex, lines[nextIndex].indent)
    }

    private fun parseIfChain(lines: List<SourceLine>, startIndex: Int, baseIndent: Int): Pair<Stmt.IfBlock, Int> {
        val branches = mutableListOf<Stmt.IfBranch>()
        val first = lines[startIndex]
        if (!first.content.endsWith(":")) {
            throw EvalException("SyntaxError di baris ${first.line}: pastikan ada tanda ':' setelah kondisi if.")
        }
        val firstCond = first.content.removePrefix("if").removeSuffix(":").trim()
        val (firstBody, idxAfterIf) = parseIndentedChildBlock(lines, startIndex + 1, baseIndent, first.line)
        branches += Stmt.IfBranch(firstCond, firstBody)

        var i = idxAfterIf
        while (i < lines.size && lines[i].indent == baseIndent) {
            val cur = lines[i]
            val text = cur.content
            if (text.startsWith("elif ") || text.startsWith("elif(")) {
                if (!text.endsWith(":")) throw EvalException("SyntaxError di baris ${cur.line}: tambahkan ':' setelah elif.")
                val cond = text.removePrefix("elif").removeSuffix(":").trim()
                val (body, nextIdx) = parseIndentedChildBlock(lines, i + 1, baseIndent, cur.line)
                branches += Stmt.IfBranch(cond, body)
                i = nextIdx
            } else if (text == "else:" || text == "else :") {
                val (body, nextIdx) = parseIndentedChildBlock(lines, i + 1, baseIndent, cur.line)
                branches += Stmt.IfBranch(null, body)
                i = nextIdx
                break
            } else {
                break
            }
        }
        return Stmt.IfBlock(first.line, branches) to i
    }

    private fun parseSimpleStmt(line: SourceLine): Stmt {
        val text = line.content
        for (op in listOf("+=", "-=", "*=", "/=")) {
            val idx = findTopLevelToken(text, op)
            if (idx > 0) {
                val name = text.substring(0, idx).trim()
                val expr = text.substring(idx + op.length).trim()
                if (!isValidIdentifier(name) || expr.isEmpty()) {
                    throw EvalException("SyntaxError di baris ${line.line}: assignment tidak valid.")
                }
                return Stmt.AugAssign(line.line, name, op, expr)
            }
        }
        val eqIdx = findAssignmentEquals(text)
        if (eqIdx >= 0) {
            val name = text.substring(0, eqIdx).trim()
            val expr = text.substring(eqIdx + 1).trim()
            if (!isValidIdentifier(name) || expr.isEmpty()) {
                throw EvalException("SyntaxError di baris ${line.line}: isi nilai variabel setelah '='.")
            }
            return Stmt.Assign(line.line, name, expr)
        }
        return Stmt.ExprOnly(line.line, text)
    }

    private fun executeBlock(
        stmts: List<Stmt>,
        scope: MutableMap<String, Any?>,
        functions: MutableMap<String, Stmt.DefFunc>,
        stdout: MutableList<String>,
        tick: (Int) -> Unit
    ) {
        for (stmt in stmts) {
            tick(stmt.line)
            when (stmt) {
                is Stmt.Assign -> {
                    scope[stmt.name] = evalExpr(stmt.expr, stmt.line, scope, functions, stdout, tick)
                }
                is Stmt.AugAssign -> {
                    val current = scope[stmt.name]
                        ?: throw EvalException("NameError di baris ${stmt.line}: variabel '${stmt.name}' belum didefinisikan.")
                    val rhs = evalExpr(stmt.expr, stmt.line, scope, functions, stdout, tick)
                    scope[stmt.name] = when (stmt.op) {
                        "+=" -> applyPlus(current, rhs, stmt.line)
                        "-=" -> applyNumericOp(current, rhs, stmt.line) { a, b -> a - b }
                        "*=" -> applyNumericOp(current, rhs, stmt.line) { a, b -> a * b }
                        "/=" -> {
                            val d = toDouble(rhs, stmt.line)
                            if (d == 0.0) throw EvalException("ZeroDivisionError di baris ${stmt.line}: pembagian dengan nol.")
                            toDouble(current, stmt.line) / d
                        }
                        else -> current
                    }
                }
                is Stmt.ExprOnly -> {
                    evalExpr(stmt.expr, stmt.line, scope, functions, stdout, tick)
                }
                is Stmt.Return -> {
                    val value = stmt.expr?.let { evalExpr(it, stmt.line, scope, functions, stdout, tick) }
                    throw ReturnSignal(value)
                }
                is Stmt.IfBlock -> {
                    for (branch in stmt.branches) {
                        val matched = if (branch.condition == null) {
                            true
                        } else {
                            isTruthy(evalExpr(branch.condition, stmt.line, scope, functions, stdout, tick))
                        }
                        if (matched) {
                            executeBlock(branch.body, scope, functions, stdout, tick)
                            break
                        }
                    }
                }
                is Stmt.ForRange -> {
                    val iterable = evalExpr(stmt.iterableExpr, stmt.line, scope, functions, stdout, tick)
                    val items = iterable as? List<*>
                        ?: throw EvalException("TypeError di baris ${stmt.line}: objek tidak dapat diiterasi dalam 'for'.")
                    for (item in items) {
                        tick(stmt.line)
                        scope[stmt.varName] = item
                        executeBlock(stmt.body, scope, functions, stdout, tick)
                    }
                }
                is Stmt.WhileLoop -> {
                    while (isTruthy(evalExpr(stmt.condition, stmt.line, scope, functions, stdout, tick))) {
                        tick(stmt.line)
                        executeBlock(stmt.body, scope, functions, stdout, tick)
                    }
                }
                is Stmt.DefFunc -> {
                    functions[stmt.name] = stmt
                }
            }
        }
    }

    private fun evalExpr(
        expr: String,
        line: Int,
        scope: MutableMap<String, Any?>,
        functions: MutableMap<String, Stmt.DefFunc>,
        stdout: MutableList<String>,
        tick: (Int) -> Unit
    ): Any? {
        val tokens = tokenize(expr, line)
        val parser = ExprParser(tokens, line, scope, functions, stdout, tick)
        val value = parser.parseOr()
        if (!parser.isAtEnd()) {
            throw EvalException("SyntaxError di baris $line: token tidak terduga '${parser.peek().text}'.")
        }
        return value
    }

    private enum class TokType { NUM, STR, ID, OP, LPAREN, RPAREN, LBRACK, RBRACK, COMMA }
    private data class Tok(val type: TokType, val text: String)

    private fun tokenize(input: String, line: Int): List<Tok> {
        val out = mutableListOf<Tok>()
        var i = 0
        while (i < input.length) {
            val ch = input[i]
            when {
                ch.isWhitespace() -> i++
                ch == '"' || ch == '\'' -> {
                    val quote = ch
                    i++
                    val sb = StringBuilder()
                    var closed = false
                    while (i < input.length) {
                        val c = input[i]
                        if (c == '\\' && i + 1 < input.length) {
                            sb.append(input[i + 1])
                            i += 2
                        } else if (c == quote) {
                            closed = true
                            i++
                            break
                        } else {
                            sb.append(c)
                            i++
                        }
                    }
                    if (!closed) throw EvalException("SyntaxError di baris $line: tanda kutip string belum ditutup.")
                    out += Tok(TokType.STR, sb.toString())
                }
                ch.isDigit() -> {
                    val start = i
                    var hasDot = false
                    while (i < input.length && (input[i].isDigit() || (input[i] == '.' && !hasDot))) {
                        if (input[i] == '.') hasDot = true
                        i++
                    }
                    out += Tok(TokType.NUM, input.substring(start, i))
                }
                ch.isLetter() || ch == '_' -> {
                    val start = i
                    while (i < input.length && (input[i].isLetterOrDigit() || input[i] == '_')) i++
                    val word = input.substring(start, i)
                    if (word in setOf("and", "or", "not", "in")) {
                        out += Tok(TokType.OP, word)
                    } else {
                        out += Tok(TokType.ID, word)
                    }
                }
                ch == '(' -> { out += Tok(TokType.LPAREN, "("); i++ }
                ch == ')' -> { out += Tok(TokType.RPAREN, ")"); i++ }
                ch == '[' -> { out += Tok(TokType.LBRACK, "["); i++ }
                ch == ']' -> { out += Tok(TokType.RBRACK, "]"); i++ }
                ch == ',' -> { out += Tok(TokType.COMMA, ","); i++ }
                else -> {
                    val two = if (i + 1 < input.length) input.substring(i, i + 2) else ""
                    if (two in setOf("==", "!=", ">=", "<=", "//")) {
                        out += Tok(TokType.OP, two)
                        i += 2
                    } else if (ch in setOf('+', '-', '*', '/', '%', '>', '<')) {
                        out += Tok(TokType.OP, ch.toString())
                        i++
                    } else {
                        throw EvalException("SyntaxError di baris $line: karakter tidak dikenali '$ch'.")
                    }
                }
            }
        }
        return out
    }

    private inner class ExprParser(
        private val tokens: List<Tok>,
        private val line: Int,
        private val scope: MutableMap<String, Any?>,
        private val functions: MutableMap<String, Stmt.DefFunc>,
        private val stdout: MutableList<String>,
        private val tick: (Int) -> Unit
    ) {
        private var pos = 0

        fun isAtEnd() = pos >= tokens.size
        fun peek(): Tok = tokens[pos]
        private fun matchOp(vararg ops: String): String? {
            if (!isAtEnd() && tokens[pos].type == TokType.OP && tokens[pos].text in ops) {
                return tokens[pos++].text
            }
            return null
        }

        fun parseOr(): Any? {
            var left = parseAnd()
            while (matchOp("or") != null) {
                val right = parseAnd()
                left = if (isTruthy(left)) left else right
            }
            return left
        }

        private fun parseAnd(): Any? {
            var left = parseNot()
            while (matchOp("and") != null) {
                val right = parseNot()
                left = if (!isTruthy(left)) left else right
            }
            return left
        }

        private fun parseNot(): Any? {
            return if (matchOp("not") != null) !isTruthy(parseNot()) else parseComparison()
        }

        private fun parseComparison(): Any? {
            var left = parseAdditive()
            while (true) {
                val op = matchOp("==", "!=", ">", "<", ">=", "<=", "in") ?: break
                val right = parseAdditive()
                left = when (op) {
                    "==" -> valuesMatch(left, right)
                    "!=" -> !valuesMatch(left, right)
                    ">" -> compareValues(left, right, line) > 0
                    "<" -> compareValues(left, right, line) < 0
                    ">=" -> compareValues(left, right, line) >= 0
                    "<=" -> compareValues(left, right, line) <= 0
                    "in" -> when (right) {
                        is List<*> -> right.any { valuesMatch(it, left) }
                        is String -> left is String && right.contains(left)
                        else -> throw EvalException("TypeError di baris $line: operator 'in' memerlukan list atau string.")
                    }
                    else -> false
                }
            }
            return left
        }

        private fun parseAdditive(): Any? {
            var left = parseMultiplicative()
            while (true) {
                val op = matchOp("+", "-") ?: break
                val right = parseMultiplicative()
                left = if (op == "+") applyPlus(left, right, line)
                else applyNumericOp(left, right, line) { a, b -> a - b }
            }
            return left
        }

        private fun parseMultiplicative(): Any? {
            var left = parseUnary()
            while (true) {
                val op = matchOp("*", "/", "//", "%") ?: break
                val right = parseUnary()
                left = when (op) {
                    "*" -> applyNumericOp(left, right, line) { a, b -> a * b }
                    "/" -> {
                        val d = toDouble(right, line)
                        if (d == 0.0) throw EvalException("ZeroDivisionError di baris $line: pembagian dengan nol.")
                        toDouble(left, line) / d
                    }
                    "//" -> {
                        val b = toLong(right, line)
                        if (b == 0L) throw EvalException("ZeroDivisionError di baris $line: pembagian dengan nol.")
                        (toLong(left, line) / b).toInt()
                    }
                    "%" -> {
                        val b = toLong(right, line)
                        if (b == 0L) throw EvalException("ZeroDivisionError di baris $line: modulo dengan nol.")
                        (toLong(left, line) % b).toInt()
                    }
                    else -> left
                }
            }
            return left
        }

        private fun parseUnary(): Any? {
            val op = matchOp("-", "+")
            if (op != null) {
                val v = parseUnary()
                return if (op == "-") {
                    when (v) {
                        is Int -> -v
                        is Double -> -v
                        else -> throw EvalException("TypeError di baris $line: operator '-' tidak mendukung tipe ini.")
                    }
                } else v
            }
            return parsePostfix()
        }

        private fun parsePostfix(): Any? {
            var value = parsePrimary()
            while (!isAtEnd() && tokens[pos].type == TokType.LBRACK) {
                pos++ // '['
                val idxVal = parseOr()
                if (isAtEnd() || tokens[pos].type != TokType.RBRACK) {
                    throw EvalException("SyntaxError di baris $line: kurung siku ']' belum ditutup.")
                }
                pos++
                val idx = (idxVal as? Number)?.toInt()
                    ?: throw EvalException("TypeError di baris $line: indeks list harus angka.")
                value = when (value) {
                    is List<*> -> {
                        val realIdx = if (idx < 0) value.size + idx else idx
                        value.getOrElse(realIdx) { throw EvalException("IndexError di baris $line: indeks di luar batas.") }
                    }
                    is String -> {
                        val realIdx = if (idx < 0) value.length + idx else idx
                        value.getOrNull(realIdx)?.toString() ?: throw EvalException("IndexError di baris $line: indeks string di luar batas.")
                    }
                    else -> throw EvalException("TypeError di baris $line: objek tidak mendukung indexing.")
                }
            }
            return value
        }

        private fun parsePrimary(): Any? {
            if (isAtEnd()) throw EvalException("SyntaxError di baris $line: ekspresi tidak lengkap.")
            val tok = tokens[pos++]
            return when (tok.type) {
                TokType.NUM -> if (tok.text.contains('.')) tok.text.toDouble() else tok.text.toInt()
                TokType.STR -> tok.text
                TokType.LPAREN -> {
                    val v = parseOr()
                    if (isAtEnd() || tokens[pos].type != TokType.RPAREN) {
                        throw EvalException("SyntaxError di baris $line: tanda kurung ')' belum ditutup.")
                    }
                    pos++
                    v
                }
                TokType.LBRACK -> {
                    val list = mutableListOf<Any?>()
                    if (!isAtEnd() && tokens[pos].type != TokType.RBRACK) {
                        list += parseOr()
                        while (!isAtEnd() && tokens[pos].type == TokType.COMMA) {
                            pos++
                            if (!isAtEnd() && tokens[pos].type == TokType.RBRACK) break
                            list += parseOr()
                        }
                    }
                    if (isAtEnd() || tokens[pos].type != TokType.RBRACK) {
                        throw EvalException("SyntaxError di baris $line: kurung siku ']' belum ditutup.")
                    }
                    pos++
                    list
                }
                TokType.ID -> when (tok.text) {
                    "True" -> true
                    "False" -> false
                    "None" -> null
                    else -> {
                        if (!isAtEnd() && tokens[pos].type == TokType.LPAREN) {
                            pos++ // '('
                            val args = mutableListOf<Any?>()
                            if (!isAtEnd() && tokens[pos].type != TokType.RPAREN) {
                                args += parseOr()
                                while (!isAtEnd() && tokens[pos].type == TokType.COMMA) {
                                    pos++
                                    args += parseOr()
                                }
                            }
                            if (isAtEnd() || tokens[pos].type != TokType.RPAREN) {
                                throw EvalException("SyntaxError di baris $line: tanda kurung ')' pada pemanggilan fungsi belum ditutup.")
                            }
                            pos++
                            invokeFunction(tok.text, args)
                        } else {
                            if (!scope.containsKey(tok.text)) {
                                throw EvalException("NameError di baris $line: variabel '${tok.text}' belum didefinisikan.")
                            }
                            scope[tok.text]
                        }
                    }
                }
                else -> throw EvalException("SyntaxError di baris $line: token tidak terduga '${tok.text}'.")
            }
        }

        private fun invokeFunction(name: String, args: List<Any?>): Any? {
            return when (name) {
                "print" -> {
                    stdout += args.joinToString(" ") { formatPy(it) }
                    null
                }
                "len" -> {
                    val target = args.singleOrNull() ?: throw EvalException("TypeError di baris $line: len() butuh 1 argumen.")
                    when (target) {
                        is String -> target.length
                        is List<*> -> target.size
                        else -> throw EvalException("TypeError di baris $line: objek tidak punya len().")
                    }
                }
                "range" -> buildRange(args)
                "int" -> when (val a = args.singleOrNull()) {
                    is Number -> a.toInt()
                    is String -> a.trim().toIntOrNull() ?: throw EvalException("ValueError di baris $line: gagal ubah '$a' ke int.")
                    is Boolean -> if (a) 1 else 0
                    else -> throw EvalException("TypeError di baris $line: int() argumen tidak valid.")
                }
                "float" -> when (val a = args.singleOrNull()) {
                    is Number -> a.toDouble()
                    is String -> a.trim().toDoubleOrNull() ?: throw EvalException("ValueError di baris $line: gagal ubah '$a' ke float.")
                    else -> throw EvalException("TypeError di baris $line: float() argumen tidak valid.")
                }
                "str" -> formatPy(args.singleOrNull())
                "bool" -> isTruthy(args.singleOrNull())
                else -> {
                    val fn = functions[name]
                        ?: throw EvalException("NameError di baris $line: fungsi '$name' tidak dikenal.")
                    if (args.size != fn.params.size) {
                        throw EvalException("TypeError di baris $line: fungsi '$name' butuh ${fn.params.size} argumen, diberikan ${args.size}.")
                    }
                    val localScope = scope.toMutableMap()
                    fn.params.forEachIndexed { idx, p -> localScope[p] = args[idx] }
                    try {
                        executeBlock(fn.body, localScope, functions, stdout, tick)
                        null
                    } catch (ret: ReturnSignal) {
                        ret.value
                    }
                }
            }
        }

        private fun buildRange(args: List<Any?>): List<Int> {
            val nums = args.map { (it as? Number)?.toInt() ?: throw EvalException("TypeError di baris $line: argumen range() harus integer.") }
            val (start, stop, step) = when (nums.size) {
                1 -> Triple(0, nums[0], 1)
                2 -> Triple(nums[0], nums[1], 1)
                3 -> Triple(nums[0], nums[1], nums[2])
                else -> throw EvalException("TypeError di baris $line: range() butuh 1 sampai 3 argumen.")
            }
            if (step == 0) throw EvalException("ValueError di baris $line: step range() tidak boleh 0.")
            val out = mutableListOf<Int>()
            var cur = start
            while ((step > 0 && cur < stop) || (step < 0 && cur > stop)) {
                out += cur
                if (out.size > MAX_STEPS) throw EvalException("RuntimeError di baris $line: ukuran range() terlalu besar.")
                cur += step
            }
            return out
        }
    }

    private fun applyPlus(left: Any?, right: Any?, line: Int): Any {
        return when {
            left is String && right is String -> left + right
            left is List<*> && right is List<*> -> left + right
            left is Number && right is Number -> applyNumericOp(left, right, line) { a, b -> a + b }
            else -> throw EvalException("TypeError di baris $line: operator '+' tidak cocok untuk tipe ${formatPy(left)} dan ${formatPy(right)}.")
        }
    }

    private fun applyNumericOp(left: Any?, right: Any?, line: Int, op: (Double, Double) -> Double): Any {
        if (left !is Number || right !is Number || left is Boolean || right is Boolean) {
            throw EvalException("TypeError di baris $line: operasi aritmatika memerlukan angka.")
        }
        val res = op(left.toDouble(), right.toDouble())
        return if (left is Int && right is Int && res % 1.0 == 0.0 && abs(res) <= Int.MAX_VALUE) res.toInt() else res
    }

    private fun compareValues(left: Any?, right: Any?, line: Int): Int {
        if (left is Number && right is Number) {
            return left.toDouble().compareTo(right.toDouble())
        }
        if (left is String && right is String) {
            return left.compareTo(right)
        }
        throw EvalException("TypeError di baris $line: tipe tidak dapat dibandingkan.")
    }

    private fun valuesMatch(a: Any?, b: Any?): Boolean {
        if (a is Number && b is Number) return a.toDouble() == b.toDouble()
        return a == b
    }

    private fun toDouble(v: Any?, line: Int): Double =
        (v as? Number)?.toDouble() ?: throw EvalException("TypeError di baris $line: nilai harus berupa angka.")

    private fun toLong(v: Any?, line: Int): Long =
        (v as? Number)?.toLong() ?: throw EvalException("TypeError di baris $line: nilai harus berupa integer.")

    private fun isTruthy(v: Any?): Boolean = when (v) {
        null -> false
        is Boolean -> v
        is Number -> v.toDouble() != 0.0
        is String -> v.isNotEmpty()
        is List<*> -> v.isNotEmpty()
        else -> true
    }

    private fun formatPy(v: Any?): String = when (v) {
        null -> "None"
        true -> "True"
        false -> "False"
        is List<*> -> v.joinToString(", ", "[", "]") { formatPy(it) }
        else -> v.toString()
    }

    private fun isValidIdentifier(name: String): Boolean {
        if (name.isEmpty()) return false
        if (!(name[0].isLetter() || name[0] == '_')) return false
        return name.all { it.isLetterOrDigit() || it == '_' }
    }

    private fun findAssignmentEquals(text: String): Int {
        var inSingle = false
        var inDouble = false
        var parenDepth = 0
        for (i in text.indices) {
            val ch = text[i]
            if (ch == '\'' && !inDouble) inSingle = !inSingle
            else if (ch == '"' && !inSingle) inDouble = !inDouble
            else if (!inSingle && !inDouble) {
                when (ch) {
                    '(', '[' -> parenDepth++
                    ')', ']' -> parenDepth--
                    '=' -> if (parenDepth == 0) {
                        val prev = text.getOrNull(i - 1)
                        val next = text.getOrNull(i + 1)
                        if (prev !in setOf('=', '!', '<', '>', '+', '-', '*', '/') && next != '=') {
                            return i
                        }
                    }
                }
            }
        }
        return -1
    }

    private fun findTopLevelToken(text: String, token: String): Int {
        var inSingle = false
        var inDouble = false
        for (i in 0..(text.length - token.length)) {
            val ch = text[i]
            if (ch == '\'' && !inDouble) inSingle = !inSingle
            else if (ch == '"' && !inSingle) inDouble = !inDouble
            else if (!inSingle && !inDouble && text.startsWith(token, i)) return i
        }
        return -1
    }
}
