package com.example.codeofthemistery.domain.mechanics

import kotlin.math.max
import kotlin.math.min

/** Pure domain calculator for 6/6 HP battle damage, Firewall Shield mitigation, and Syntax Potion healing. */
object BattleCalculator {
    const val MAX_HP = 6
    const val SYNTAX_POTION_HEAL = 4

    data class DamageResult(
        val newHp: Int,
        val damageTaken: Int,
        val shieldBlocked: Boolean,
        val shieldConsumedCharge: Boolean,
        val isDead: Boolean
    )

    fun applyWrongAnswer(
        currentHp: Int,
        isBossBattle: Boolean,
        hasFirewallShield: Boolean,
        shieldChargeUsed: Boolean
    ): DamageResult {
        val baseDamage = if (isBossBattle) 2 else 1
        if (hasFirewallShield && !shieldChargeUsed) {
            return DamageResult(
                newHp = currentHp,
                damageTaken = 0,
                shieldBlocked = true,
                shieldConsumedCharge = true,
                isDead = currentHp <= 0
            )
        }
        val actualDamage = if (hasFirewallShield && isBossBattle) max(1, baseDamage - 1) else baseDamage
        val nextHp = max(0, currentHp - actualDamage)
        return DamageResult(
            newHp = nextHp,
            damageTaken = actualDamage,
            shieldBlocked = false,
            shieldConsumedCharge = false,
            isDead = nextHp == 0
        )
    }

    fun applySyntaxPotion(currentHp: Int): Int =
        min(MAX_HP, max(0, currentHp) + SYNTAX_POTION_HEAL)
}
