package com.example.codeofthemistery.feature.battle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.BorderHighlight
import com.example.codeofthemistery.core.theme.BorderMuted
import com.example.codeofthemistery.core.theme.CrimsonDanger
import com.example.codeofthemistery.core.theme.CyanTerminal
import com.example.codeofthemistery.core.theme.GoldAccent
import com.example.codeofthemistery.core.theme.MonoFontFamily
import com.example.codeofthemistery.core.theme.OnPrimaryDark
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SuccessGreen
import com.example.codeofthemistery.core.theme.SurfaceCard
import com.example.codeofthemistery.core.theme.SurfaceElevated
import com.example.codeofthemistery.core.theme.TextMuted
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.core.theme.TextSecondary
import com.example.codeofthemistery.core.ui.CyberCard
import com.example.codeofthemistery.core.ui.NeonButton
import com.example.codeofthemistery.core.ui.TerminalBadge
import com.example.codeofthemistery.domain.evaluator.ExecutionResult
import com.example.codeofthemistery.domain.model.OwnedItem
import com.example.codeofthemistery.domain.model.SceneStep
import com.example.codeofthemistery.feature.items.itemAccentColor
import com.example.codeofthemistery.feature.items.itemIcon
import kotlinx.coroutines.delay

@Composable
fun VisualNovelPanel(
    step: SceneStep.NarrationStep,
    clueToast: String?,
    onAdvance: () -> Unit
) {
    var visibleChars by remember(step.id) { mutableIntStateOf(0) }
    val fullText = step.text
    val isFinished = visibleChars >= fullText.length

    LaunchedEffect(step.id) {
        visibleChars = 0
        while (visibleChars < fullText.length) {
            delay(16)
            visibleChars = (visibleChars + 2).coerceAtMost(fullText.length)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (!isFinished) visibleChars = fullText.length else onAdvance()
            }
    ) {
        clueToast?.let {
            TerminalBadge(text = it, color = CyanTerminal)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                shape = RoundedCornerShape(14.dp),
                color = Color.Black,
                border = BorderStroke(1.5.dp, TextPrimary)
            ) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 14.dp)) {
                    Text(
                        text = fullText.take(visibleChars),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        modifier = Modifier.heightIn(min = 84.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isFinished) "Tap untuk lanjut >>" else "Tap untuk percepat...",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryLavender,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            Surface(
                modifier = Modifier.padding(start = 16.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color.Black,
                border = BorderStroke(1.5.dp, TextPrimary)
            ) {
                Text(
                    text = step.speaker,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryLavender,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun QuizBattlePanel(
    step: SceneStep.QuizBattleStep,
    selectedOption: Int?,
    isPassed: Boolean,
    feedback: String?,
    onSelectOption: (Int) -> Unit,
    onContinue: () -> Unit
) {
    CyberCard(borderColor = if (isPassed) SuccessGreen else PrimaryLavender.copy(alpha = 0.5f)) {
        TerminalBadge(
            text = if (step.isBoss) "BOSS QUIZ BATTLE" else "QUIZ BATTLE",
            color = if (step.isBoss) GoldAccent else PrimaryLavender
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = step.prompt, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        step.options.forEachIndexed { idx, optionText ->
            val isSelected = selectedOption == idx
            val borderCol = when {
                isPassed && idx == step.correctIndex -> SuccessGreen
                isSelected && idx != step.correctIndex -> CrimsonDanger
                else -> BorderHighlight
            }
            val bgCol = when {
                isPassed && idx == step.correctIndex -> SuccessGreen.copy(alpha = 0.14f)
                isSelected && idx != step.correctIndex -> CrimsonDanger.copy(alpha = 0.14f)
                else -> BackgroundPrimary
            }
            Surface(
                onClick = { if (!isPassed) onSelectOption(idx) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                color = bgCol,
                border = BorderStroke(1.dp, borderCol)
            ) {
                Text(
                    text = "${'A' + idx}.  $optionText",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                )
            }
        }

        feedback?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = if (isPassed) SuccessGreen else CrimsonDanger
            )
        }

        if (isPassed) {
            Spacer(modifier = Modifier.height(12.dp))
            NeonButton(
                text = "Lanjut ke Tantangan Berikutnya >>",
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PythonCodeEditorPanel(
    step: SceneStep.CodeBattleStep,
    codeText: String,
    executionResult: ExecutionResult?,
    isShowingOutput: Boolean,
    showHint: Boolean,
    hasLaptop: Boolean,
    isEvaluating: Boolean,
    onCodeChange: (String) -> Unit,
    onRunClick: () -> Unit,
    onResetClick: () -> Unit,
    onHintClick: () -> Unit,
    onBackToEditor: () -> Unit,
    onContinueAfterSuccess: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        CyberCard(containerColor = SurfaceCard, contentPadding = PaddingValues(12.dp)) {
            Text(text = step.title, style = MaterialTheme.typography.titleSmall, color = PrimaryLavender)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = step.lessonSummary, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            if (showHint && hasLaptop) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoldAccent.copy(alpha = 0.12f))
                        .border(1.dp, GoldAccent.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text("Hint Laptop: ${step.hintText}", style = MaterialTheme.typography.bodySmall, color = GoldAccent)
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF101218),
            border = BorderStroke(1.dp, BorderHighlight)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceCard)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Code, contentDescription = null, tint = PrimaryLavender, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isShowingOutput) "OUTPUT" else "MAIN.PY",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary
                        )
                    }
                    Row {
                        if (hasLaptop && !isShowingOutput) {
                            IconButton(onClick = onHintClick, modifier = Modifier.size(30.dp)) {
                                Icon(Icons.Rounded.Lightbulb, contentDescription = "Hint", tint = GoldAccent, modifier = Modifier.size(18.dp))
                            }
                        }
                        if (!isShowingOutput) {
                            IconButton(onClick = onResetClick, modifier = Modifier.size(30.dp)) {
                                Icon(Icons.Rounded.Refresh, contentDescription = "Reset", tint = TextMuted, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
                HorizontalDivider(color = BorderMuted)

                if (!isShowingOutput) {
                    val quickTokens = listOf("=", ":", "\"\"", "()", ">", "    ")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickTokens.forEach { tok ->
                            Surface(
                                onClick = { onCodeChange(codeText + tok) },
                                shape = RoundedCornerShape(6.dp),
                                color = SurfaceElevated
                            ) {
                                Text(
                                    text = if (tok == "    ") "TAB" else tok,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryLavender,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    BasicTextField(
                        value = codeText,
                        onValueChange = onCodeChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 138.dp)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        textStyle = TextStyle(
                            fontFamily = MonoFontFamily,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = TextPrimary
                        ),
                        cursorBrush = SolidColor(PrimaryLavender),
                        visualTransformation = PythonHighlightTransformation()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onRunClick,
                            enabled = !isEvaluating,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryLavender,
                                contentColor = OnPrimaryDark
                            )
                        ) {
                            Text(if (isEvaluating) "Running..." else "Jalankan", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    val res = executionResult
                    val passed = res?.isRulePassed == true
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (passed) "Code Executed Successfully With Exit Code 0;"
                            else res?.errorMessage ?: "Execution Failed",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = if (passed) SuccessGreen else CrimsonDanger
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        if (res != null && res.stdoutLines.isNotEmpty()) {
                            res.stdoutLines.forEach { line ->
                                Text(
                                    text = "> $line",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                            }
                        } else {
                            Text(
                                text = "> (tidak ada output stdout)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
                        ) {
                            OutlinedButton(onClick = onBackToEditor) {
                                Text("Edit MAIN.PY", color = TextSecondary)
                            }
                            if (passed) {
                                Button(
                                    onClick = onContinueAfterSuccess,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PrimaryLavender,
                                        contentColor = OnPrimaryDark
                                    )
                                ) {
                                    Text("Lanjut >>", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private class PythonHighlightTransformation : VisualTransformation {
    private val keywords = setOf("if", "elif", "else", "for", "in", "range", "while", "def", "return", "print", "and", "or", "not", "True", "False", "None")

    override fun filter(text: AnnotatedString): TransformedText {
        val src = text.text
        val highlighted = buildAnnotatedString {
            append(src)
            var idx = 0
            while (idx < src.length) {
                when {
                    src[idx] == '#' -> {
                        val end = src.indexOf('\n', idx).let { if (it < 0) src.length else it }
                        addStyle(SpanStyle(color = TextMuted), idx, end)
                        idx = end
                    }
                    src[idx] == '"' || src[idx] == '\'' -> {
                        val quote = src[idx]
                        var end = idx + 1
                        while (end < src.length && src[end] != quote && src[end] != '\n') end++
                        if (end < src.length && src[end] == quote) end++
                        addStyle(SpanStyle(color = Color(0xFFFF8F9C)), idx, end)
                        idx = end
                    }
                    src[idx].isLetter() || src[idx] == '_' -> {
                        var end = idx + 1
                        while (end < src.length && (src[end].isLetterOrDigit() || src[end] == '_')) end++
                        val word = src.substring(idx, end)
                        if (word in keywords) {
                            addStyle(SpanStyle(color = PrimaryLavender, fontWeight = FontWeight.Bold), idx, end)
                        }
                        idx = end
                    }
                    src[idx].isDigit() -> {
                        var end = idx + 1
                        while (end < src.length && (src[end].isDigit() || src[end] == '.')) end++
                        addStyle(SpanStyle(color = CyanTerminal), idx, end)
                        idx = end
                    }
                    else -> idx++
                }
            }
        }
        return TransformedText(highlighted, OffsetMapping.Identity)
    }
}

@Composable
fun QuickItemBar(
    slots: List<OwnedItem?>,
    onItemClick: (OwnedItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        slots.forEach { item ->
            val accent = item?.let { itemAccentColor(it.itemId, it.rarity) } ?: BorderHighlight
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(enabled = item != null) { item?.let(onItemClick) }
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceCard)
                        .border(1.dp, if (item != null) accent.copy(alpha = 0.65f) else BorderHighlight, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (item != null) {
                        Icon(
                            imageVector = itemIcon(item.itemId),
                            contentDescription = item.name,
                            tint = accent,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (item != null && item.itemId == "item_syntax_potion") "${item.name} (x${item.quantity})"
                    else item?.name ?: "Kosong",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (item != null) TextSecondary else TextMuted
                )
            }
        }
    }
}
