package com.example.codeofthemistery.core.util

import java.security.MessageDigest
import kotlin.random.Random

/** Cryptographic and random code utilities for offline authentication and voucher generation. */
object HashUtil {
    private const val SALT_PREFIX = "cotm_v1_"
    private val VOUCHER_CHARS = ('A'..'Z') + ('0'..'9')

    fun hashPassword(rawPassword: String, email: String): String {
        val input = "$SALT_PREFIX${email.trim().lowercase()}:$rawPassword"
        val digest = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun generateOtp(): String = Random.nextInt(100000, 999999).toString()

    fun generateVoucherCode(partnerCode: String): String {
        val prefix = partnerCode.uppercase().take(3).padEnd(3, 'X')
        val suffix = (1..4).map { VOUCHER_CHARS.random() }.joinToString("")
        val block = (1..4).map { VOUCHER_CHARS.random() }.joinToString("")
        return "COTM-$prefix-$block-$suffix"
    }
}
