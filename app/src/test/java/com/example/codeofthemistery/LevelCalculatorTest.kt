package com.example.codeofthemistery

import com.example.codeofthemistery.domain.mechanics.LevelCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class LevelCalculatorTest {

    @Test
    fun `adding xp levels up player across multiple thresholds`() {
        // Level 1 needs 750 XP, Level 2 needs 1500 XP
        val res1 = LevelCalculator.addXp(currentLevel = 1, currentXp = 300, earnedXp = 500)
        assertEquals(2, res1.level)
        assertEquals(50, res1.currentXp)
        assertEquals(1500, res1.nextLevelXp)

        val res2 = LevelCalculator.addXp(currentLevel = 2, currentXp = 697, earnedXp = 900)
        assertEquals(3, res2.level)
        assertEquals(97, res2.currentXp)
        assertEquals("SYNTAX HUNTER", LevelCalculator.rankTitleForLevel(res2.level))
    }
}
