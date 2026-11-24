package com.example.codeofthemistery

import com.example.codeofthemistery.core.util.HashUtil
import com.example.codeofthemistery.domain.mechanics.AchievementEngine
import com.example.codeofthemistery.domain.model.AchievementProgress
import com.example.codeofthemistery.domain.model.GameEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AchievementAndHashTest {

    @Test
    fun `password hash is deterministic per email and differs across emails`() {
        val h1 = HashUtil.hashPassword("secret123", "User@Unsri.ac.id")
        val h2 = HashUtil.hashPassword("secret123", "user@unsri.ac.id")
        val h3 = HashUtil.hashPassword("secret123", "other@unsri.ac.id")
        assertEquals(h1, h2)
        assertNotEquals(h1, h3)

        val code = HashUtil.generateVoucherCode("shopee")
        assertTrue(code.startsWith("COTM-SHO-"))
    }

    @Test
    fun `achievement engine unlocks variable explorer after target passes`() {
        val initial = listOf(
            AchievementProgress("ach_var_1", "Variable Explorer 1", "desc", 150, 1, 2, false, "explore"),
            AchievementProgress("ach_bug_hunter", "Bug Hunter", "desc", 200, 0, 1, false, "bug")
        )
        val updates = AchievementEngine.applyEvent(initial, GameEvent.CodeChallengePassed)
        assertEquals(1, updates.size)
        assertTrue(updates.first().newlyUnlocked)
        assertEquals(2, updates.first().updated.currentCount)
    }
}
