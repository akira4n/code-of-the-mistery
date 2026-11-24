package com.example.codeofthemistery

import com.example.codeofthemistery.domain.mechanics.BattleCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BattleCalculatorTest {

    @Test
    fun `wrong answer without shield reduces 1 hp in normal battle and 2 hp in boss`() {
        val normal = BattleCalculator.applyWrongAnswer(
            currentHp = 6,
            isBossBattle = false,
            hasFirewallShield = false,
            shieldChargeUsed = false
        )
        assertEquals(5, normal.newHp)
        assertEquals(1, normal.damageTaken)
        assertFalse(normal.shieldBlocked)

        val boss = BattleCalculator.applyWrongAnswer(
            currentHp = 6,
            isBossBattle = true,
            hasFirewallShield = false,
            shieldChargeUsed = false
        )
        assertEquals(4, boss.newHp)
        assertEquals(2, boss.damageTaken)
    }

    @Test
    fun `firewall shield blocks first error then reduces boss damage afterward`() {
        val firstHit = BattleCalculator.applyWrongAnswer(
            currentHp = 6,
            isBossBattle = true,
            hasFirewallShield = true,
            shieldChargeUsed = false
        )
        assertEquals(6, firstHit.newHp)
        assertEquals(0, firstHit.damageTaken)
        assertTrue(firstHit.shieldBlocked)
        assertTrue(firstHit.shieldConsumedCharge)

        val secondHit = BattleCalculator.applyWrongAnswer(
            currentHp = 6,
            isBossBattle = true,
            hasFirewallShield = true,
            shieldChargeUsed = true
        )
        assertEquals(5, secondHit.newHp)
        assertEquals(1, secondHit.damageTaken)
        assertFalse(secondHit.shieldBlocked)
    }

    @Test
    fun `syntax potion heals up to max 6 hp and reaching 0 marks dead`() {
        assertEquals(6, BattleCalculator.applySyntaxPotion(4))
        assertEquals(5, BattleCalculator.applySyntaxPotion(1))

        val fatal = BattleCalculator.applyWrongAnswer(
            currentHp = 1,
            isBossBattle = false,
            hasFirewallShield = false,
            shieldChargeUsed = false
        )
        assertEquals(0, fatal.newHp)
        assertTrue(fatal.isDead)
    }
}
