package com.example.codeofthemistery.data.local.seed

import com.example.codeofthemistery.data.local.dao.AchievementDao
import com.example.codeofthemistery.data.local.dao.InventoryDao
import com.example.codeofthemistery.data.local.dao.RedeemDao
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Ensures static catalog tables (items, achievements, rewards) are seeded once idempotently. */
class CatalogSeeder(
    private val inventoryDao: InventoryDao,
    private val achievementDao: AchievementDao,
    private val redeemDao: RedeemDao
) {
    private val mutex = Mutex()
    @Volatile
    private var seeded = false

    suspend fun ensureSeeded() {
        if (seeded) return
        mutex.withLock {
            if (seeded) return
            inventoryDao.upsertCatalogItems(AppSeedData.itemsCatalog)
            achievementDao.upsertCatalog(AppSeedData.achievementsCatalog)
            redeemDao.insertCatalogIfAbsent(AppSeedData.rewardsCatalog)
            seeded = true
        }
    }
}
