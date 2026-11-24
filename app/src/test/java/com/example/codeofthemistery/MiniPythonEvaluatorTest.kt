package com.example.codeofthemistery

import com.example.codeofthemistery.domain.evaluator.MiniPythonEvaluator
import com.example.codeofthemistery.domain.model.CodeValidationRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MiniPythonEvaluatorTest {

    private val evaluator = MiniPythonEvaluator()

    @Test
    fun `chapter 1 variable assignment and print executes with exit code 0`() {
        val code = """
            # Komentar diabaikan
            nama = "Dokja"
            HP = 6
            skill = "Programming"
            print("Nama:", nama)
            print("HP:", HP)
            print("Skill:", skill)
        """.trimIndent()
        val rule = CodeValidationRule(
            nonEmptyStringVars = listOf("nama", "skill"),
            expectedVars = mapOf("HP" to 6),
            minStdoutLines = 3
        )

        val result = evaluator.evaluate(code, rule)
        assertTrue(result.isSuccess)
        assertTrue(result.isRulePassed)
        assertEquals(0, result.exitCode)
        assertEquals(listOf("Nama: Dokja", "HP: 6", "Skill: Programming"), result.stdoutLines)
    }

    @Test
    fun `chapter 1 condition lowers shields and computes total_power`() {
        val code = """
            energy = 70
            shields_active = True
            if energy > 50:
                shields_active = False
                total_power = 100
            print("Power:", total_power)
        """.trimIndent()
        val rule = CodeValidationRule(
            requiredKeywords = listOf("if"),
            expectedVars = mapOf("shields_active" to false, "total_power" to 100),
            expectedStdoutContains = listOf("Power:", "100")
        )

        val result = evaluator.evaluate(code, rule)
        assertTrue(result.isSuccess)
        assertTrue(result.isRulePassed)
        assertEquals(false, result.variables["shields_active"])
        assertEquals(100, result.variables["total_power"])
    }

    @Test
    fun `chapter 2 if elif else chooses correct branch`() {
        val code = """
            distance = 12
            if distance <= 15:
                mode = "STRIKE"
                damage = 80
            else:
                mode = "SCAN"
                damage = 20
            print(mode, damage)
        """.trimIndent()
        val rule = CodeValidationRule(
            requiredKeywords = listOf("if", "else"),
            expectedVars = mapOf("mode" to "STRIKE", "damage" to 80)
        )

        val result = evaluator.evaluate(code, rule)
        assertTrue(result.isRulePassed)
        assertEquals("STRIKE 80", result.stdoutLines.first())
    }

    @Test
    fun `chapter 3 def function and for loop calculate boss combo damage`() {
        val code = """
            def calc_combo(base):
                return base * 2

            total_power = 0
            for i in range(3):
                total_power += calc_combo(25)

            print("Boss Damage:", total_power)
        """.trimIndent()
        val rule = CodeValidationRule(
            requiredKeywords = listOf("def", "for"),
            expectedVars = mapOf("total_power" to 150),
            expectedStdoutContains = listOf("Boss Damage:", "150")
        )

        val result = evaluator.evaluate(code, rule)
        assertTrue(result.isRulePassed)
        assertEquals(150, result.variables["total_power"])
    }

    @Test
    fun `infinite while loop is safely aborted at max steps`() {
        val code = """
            x = 0
            while True:
                x += 1
        """.trimIndent()

        val result = evaluator.evaluate(code)
        assertFalse(result.isSuccess)
        assertEquals(1, result.exitCode)
        assertTrue(result.errorMessage?.contains("Batas eksekusi") == true)
    }

    @Test
    fun `syntax error missing colon returns informative error`() {
        val code = """
            energy = 70
            if energy > 50
                total_power = 100
        """.trimIndent()

        val result = evaluator.evaluate(code)
        assertFalse(result.isSuccess)
        assertTrue(result.errorMessage?.contains("SyntaxError") == true)
    }
}
