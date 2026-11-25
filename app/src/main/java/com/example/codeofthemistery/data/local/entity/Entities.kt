package com.example.codeofthemistery.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val level: Int,
    val currentXp: Int,
    val points: Int,
    val isDemoAccount: Boolean,
    val createdAt: Long
)

@Entity(
    tableName = "chapter_progress",
    primaryKeys = ["userId", "chapterId"]
)
data class ChapterProgressEntity(
    val userId: String,
    val chapterId: Int,
    val status: String, // LOCKED, UNLOCKED, COMPLETED
    val bestHpRemaining: Int,
    val questionsAnswered: Int,
    val updatedAt: Long,
    val lastAlignmentChoice: String? = null
)

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val statLabel: String,
    val type: String, // EQUIPMENT, CONSUMABLE
    val rarity: String, // COMMON, RARE, EPIC
    val canUnequip: Boolean
)

@Entity(
    tableName = "user_items",
    primaryKeys = ["userId", "itemId"]
)
data class UserItemEntity(
    val userId: String,
    val itemId: String,
    val quantity: Int,
    val equippedSlot: Int?, // 1, 2, 3 or null
    val isUnlocked: Boolean
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val rewardXp: Int,
    val targetCount: Int,
    val iconKey: String
)

@Entity(
    tableName = "user_achievements",
    primaryKeys = ["userId", "achievementId"]
)
data class UserAchievementEntity(
    val userId: String,
    val achievementId: String,
    val currentCount: Int,
    val isUnlocked: Boolean,
    val unlockedAt: Long?
)

@Entity(
    tableName = "user_clues",
    primaryKeys = ["userId", "clueId"]
)
data class UserClueEntity(
    val userId: String,
    val clueId: String,
    val chapterId: Int,
    val title: String,
    val content: String,
    val discoveredAt: Long
)

@Entity(tableName = "reward_catalog")
data class RewardCatalogEntity(
    @PrimaryKey val id: String,
    val title: String,
    val partner: String,
    val description: String,
    val costPoints: Int,
    val stockRemaining: Int?
)

@Entity(tableName = "redeemed_vouchers")
data class RedeemedVoucherEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val rewardId: String,
    val rewardTitle: String,
    val partner: String,
    val voucherCode: String,
    val costPaid: Int,
    val redeemedAt: Long
)
