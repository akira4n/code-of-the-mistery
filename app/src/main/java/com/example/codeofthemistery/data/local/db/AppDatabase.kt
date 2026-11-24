package com.example.codeofthemistery.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.codeofthemistery.data.local.dao.AchievementDao
import com.example.codeofthemistery.data.local.dao.ChapterDao
import com.example.codeofthemistery.data.local.dao.InventoryDao
import com.example.codeofthemistery.data.local.dao.RedeemDao
import com.example.codeofthemistery.data.local.dao.UserDao
import com.example.codeofthemistery.data.local.entity.AchievementEntity
import com.example.codeofthemistery.data.local.entity.ChapterProgressEntity
import com.example.codeofthemistery.data.local.entity.ItemEntity
import com.example.codeofthemistery.data.local.entity.RedeemedVoucherEntity
import com.example.codeofthemistery.data.local.entity.RewardCatalogEntity
import com.example.codeofthemistery.data.local.entity.UserAchievementEntity
import com.example.codeofthemistery.data.local.entity.UserClueEntity
import com.example.codeofthemistery.data.local.entity.UserEntity
import com.example.codeofthemistery.data.local.entity.UserItemEntity

@Database(
    entities = [
        UserEntity::class,
        ChapterProgressEntity::class,
        ItemEntity::class,
        UserItemEntity::class,
        AchievementEntity::class,
        UserAchievementEntity::class,
        UserClueEntity::class,
        RewardCatalogEntity::class,
        RedeemedVoucherEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chapterDao(): ChapterDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun redeemDao(): RedeemDao
    abstract fun achievementDao(): AchievementDao
}
