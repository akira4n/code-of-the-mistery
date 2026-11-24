package com.example.codeofthemistery.domain.mechanics

/** XP progression and dynamic player rank title calculations. */
object LevelCalculator {

    data class LevelProgress(
        val level: Int,
        val currentXp: Int,
        val nextLevelXp: Int
    )

    fun xpRequiredForNextLevel(level: Int): Int = level.coerceAtLeast(1) * 750

    fun addXp(currentLevel: Int, currentXp: Int, earnedXp: Int): LevelProgress {
        var lvl = currentLevel.coerceAtLeast(1)
        var xp = (currentXp + earnedXp).coerceAtLeast(0)
        while (xp >= xpRequiredForNextLevel(lvl)) {
            xp -= xpRequiredForNextLevel(lvl)
            lvl++
        }
        return LevelProgress(level = lvl, currentXp = xp, nextLevelXp = xpRequiredForNextLevel(lvl))
    }

    fun rankTitleForLevel(level: Int): String = when {
        level <= 1 -> "INITIATE CODER"
        level == 2 -> "VIBE CODER"
        level == 3 -> "SYNTAX HUNTER"
        level == 4 -> "LOGIC ARCHITECT"
        else -> "SYSTEM MASTER"
    }
}
