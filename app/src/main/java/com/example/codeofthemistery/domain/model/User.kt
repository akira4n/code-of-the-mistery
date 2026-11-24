package com.example.codeofthemistery.domain.model

/** Active player profile and progression stats. */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val level: Int,
    val currentXp: Int,
    val points: Int,
    val isDemoAccount: Boolean = false
)
