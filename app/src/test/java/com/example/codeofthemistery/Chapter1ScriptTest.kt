package com.example.codeofthemistery

import com.example.codeofthemistery.data.local.seed.AppSeedData
import com.example.codeofthemistery.domain.evaluator.MiniPythonEvaluator
import com.example.codeofthemistery.domain.model.AlignmentChoice
import com.example.codeofthemistery.domain.model.SceneStep
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Chapter1ScriptTest {

    private val evaluator = MiniPythonEvaluator()

    @Test
    fun `chapter 1 script contains all 5 acts with unique ids and valid challenge invariants`() {
        val script = AppSeedData.getChapterScript(1)
        val ids = script.steps.map { it.id }
        assertEquals("Step IDs must be unique", ids.size, ids.toSet().size)

        val branches = script.steps.filterIsInstance<SceneStep.BranchChoiceStep>()
        assertEquals(1, branches.size)
        assertTrue(branches.first().options.any { it.alignment == AlignmentChoice.PATCH && it.unlockClue != null })
        assertTrue(branches.first().options.any { it.alignment == AlignmentChoice.PURGE && it.bonusForce > 0 })

        val bugHunts = script.steps.filterIsInstance<SceneStep.BugHuntStep>()
        assertEquals(1, bugHunts.size)
        assertTrue(bugHunts.first().bugLineIndex in bugHunts.first().codeLines.indices)

        val chipSteps = script.steps.filterIsInstance<SceneStep.CodeChipsStep>()
        assertEquals(1, chipSteps.size)
        assertEquals(
            chipSteps.first().templateSegments.size - 1,
            chipSteps.first().expectedChipsInOrder.size
        )

        val quizzes = script.steps.filterIsInstance<SceneStep.QuizBattleStep>()
        assertEquals(1, quizzes.size)
        assertTrue(quizzes.first().correctIndex in quizzes.first().options.indices)
    }

    @Test
    fun `chapter 1 coding steps reject starter blanks and pass valid solutions`() {
        val script = AppSeedData.getChapterScript(1)
        val codeSteps = script.steps.filterIsInstance<SceneStep.CodeBattleStep>()
        assertEquals(2, codeSteps.size)

        val initStep = codeSteps[0]
        assertFalse(evaluator.evaluate(initStep.starterCode, initStep.validationRule).isRulePassed)
        val solvedInit = initStep.starterCode
            .replaceFirst("____", "\"Coder\"")
            .replaceFirst("____", "6")
        assertTrue(evaluator.evaluate(solvedInit, initStep.validationRule).isRulePassed)

        val bossStep = codeSteps[1]
        assertFalse(evaluator.evaluate(bossStep.starterCode, bossStep.validationRule).isRulePassed)
        val solvedBoss = bossStep.starterCode
            .replaceFirst("____", ">")
            .replaceFirst("____", "False")
            .replaceFirst("____", "150")
        assertTrue(evaluator.evaluate(solvedBoss, bossStep.validationRule).isRulePassed)
    }
}
