package com.example.codeofthemistery.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.codeofthemistery.data.local.entity.AchievementEntity
import com.example.codeofthemistery.data.local.entity.ChapterProgressEntity
import com.example.codeofthemistery.data.local.entity.ItemEntity
import com.example.codeofthemistery.data.local.entity.RedeemedVoucherEntity
import com.example.codeofthemistery.data.local.entity.RewardCatalogEntity
import com.example.codeofthemistery.data.local.entity.UserAchievementEntity
import com.example.codeofthemistery.data.local.entity.UserClueEntity
import com.example.codeofthemistery.data.local.entity.UserEntity
import com.example.codeofthemistery.data.local.entity.UserItemEntity
import kotlinx.coroutines.flow.Flow

data class ItemJoinedRow(
    val id: String,
    val name: String,
    val description: String,
    val statLabel: String,
    val type: String,
    val rarity: String,
    val canUnequip: Boolean,
    val quantity: Int?,
    val equippedSlot: Int?,
    val isUnlocked: Boolean?
)

data class AchievementJoinedRow(
    val id: String,
    val title: String,
    val description: String,
    val rewardXp: Int,
    val targetCount: Int,
    val iconKey: String,
    val currentCount: Int?,
    val isUnlocked: Boolean?
)

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun observeUser(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserEntity)

    @Query("UPDATE users SET name = :name WHERE id = :userId")
    suspend fun updateName(userId: String, name: String)

    @Query("UPDATE users SET passwordHash = :passwordHash WHERE id = :userId")
    suspend fun updatePasswordHash(userId: String, passwordHash: String)

    @Query("UPDATE users SET level = :level, currentXp = :xp, points = points + :addedPoints WHERE id = :userId")
    suspend fun updateProgression(userId: String, level: Int, xp: Int, addedPoints: Int)

    @Query("UPDATE users SET points = points - :cost WHERE id = :userId AND points >= :cost")
    suspend fun deductPointsIfEnough(userId: String, cost: Int): Int
}

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapter_progress WHERE userId = :userId ORDER BY chapterId ASC")
    fun observeProgress(userId: String): Flow<List<ChapterProgressEntity>>

    @Query("SELECT * FROM chapter_progress WHERE userId = :userId AND chapterId = :chapterId LIMIT 1")
    suspend fun getChapterProgress(userId: String, chapterId: Int): ChapterProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(rows: List<ChapterProgressEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(row: ChapterProgressEntity)
}

@Dao
abstract class InventoryDao {
    @Query(
        """
        SELECT i.id, i.name, i.description, i.statLabel, i.type, i.rarity, i.canUnequip,
               ui.quantity, ui.equippedSlot, ui.isUnlocked
        FROM items i
        LEFT JOIN user_items ui ON i.id = ui.itemId AND ui.userId = :userId
        ORDER BY CASE i.id WHEN 'item_laptop' THEN 0 WHEN 'item_firewall_shield' THEN 1 WHEN 'item_syntax_potion' THEN 2 ELSE 3 END
        """
    )
    abstract fun observeUserInventory(userId: String): Flow<List<ItemJoinedRow>>

    @Query("SELECT * FROM user_items WHERE userId = :userId")
    abstract suspend fun getUserItems(userId: String): List<UserItemEntity>

    @Query("SELECT * FROM user_items WHERE userId = :userId AND itemId = :itemId LIMIT 1")
    abstract suspend fun getUserItem(userId: String, itemId: String): UserItemEntity?

    @Query("SELECT * FROM items")
    abstract suspend fun getAllCatalogItems(): List<ItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertCatalogItems(items: List<ItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertUserItems(items: List<UserItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertUserItem(item: UserItemEntity)

    @Query("UPDATE user_items SET equippedSlot = :slot WHERE userId = :userId AND itemId = :itemId")
    abstract suspend fun setEquippedSlot(userId: String, itemId: String, slot: Int?)

    @Query("UPDATE user_items SET quantity = quantity + :delta, isUnlocked = 1 WHERE userId = :userId AND itemId = :itemId")
    abstract suspend fun addQuantity(userId: String, itemId: String, delta: Int)
}

@Dao
abstract class RedeemDao {
    @Query("SELECT * FROM reward_catalog ORDER BY costPoints ASC")
    abstract fun observeCatalog(): Flow<List<RewardCatalogEntity>>

    @Query("SELECT * FROM reward_catalog WHERE id = :rewardId LIMIT 1")
    abstract suspend fun getRewardById(rewardId: String): RewardCatalogEntity?

    @Query("SELECT * FROM redeemed_vouchers WHERE userId = :userId ORDER BY redeemedAt DESC")
    abstract fun observeVoucherHistory(userId: String): Flow<List<RedeemedVoucherEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertCatalogIfAbsent(items: List<RewardCatalogEntity>)

    @Query("UPDATE reward_catalog SET stockRemaining = stockRemaining - 1 WHERE id = :rewardId AND stockRemaining > 0")
    abstract suspend fun decrementStockIfLimited(rewardId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertVoucher(voucher: RedeemedVoucherEntity)

    @Query("DELETE FROM redeemed_vouchers WHERE userId = :userId")
    abstract suspend fun deleteVouchersForUser(userId: String)

    @Transaction
    open suspend fun executeAtomicRedeem(
        userDao: UserDao,
        userId: String,
        reward: RewardCatalogEntity,
        voucher: RedeemedVoucherEntity
    ): Boolean {
        val deducted = userDao.deductPointsIfEnough(userId, reward.costPoints)
        if (deducted <= 0) return false
        if (reward.stockRemaining != null) {
            val stockUpdated = decrementStockIfLimited(reward.id)
            if (stockUpdated <= 0) {
                throw IllegalStateException("OUT_OF_STOCK")
            }
        }
        insertVoucher(voucher)
        return true
    }
}

@Dao
interface AchievementDao {
    @Query(
        """
        SELECT a.id, a.title, a.description, a.rewardXp, a.targetCount, a.iconKey,
               ua.currentCount, ua.isUnlocked
        FROM achievements a
        LEFT JOIN user_achievements ua ON a.id = ua.achievementId AND ua.userId = :userId
        """
    )
    fun observeAchievements(userId: String): Flow<List<AchievementJoinedRow>>

    @Query("SELECT * FROM user_clues WHERE userId = :userId ORDER BY chapterId ASC, discoveredAt ASC")
    fun observeClues(userId: String): Flow<List<UserClueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCatalog(items: List<AchievementEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserAchievements(rows: List<UserAchievementEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertClueIfNew(clue: UserClueEntity)

    @Query("DELETE FROM user_clues WHERE userId = :userId")
    suspend fun deleteCluesForUser(userId: String)
}
