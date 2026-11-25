package com.example.codeofthemistery

import com.example.codeofthemistery.core.util.AppResult
import com.example.codeofthemistery.data.local.dao.AchievementDao
import com.example.codeofthemistery.data.local.dao.AchievementJoinedRow
import com.example.codeofthemistery.data.local.dao.InventoryDao
import com.example.codeofthemistery.data.local.dao.ItemJoinedRow
import com.example.codeofthemistery.data.local.dao.RedeemDao
import com.example.codeofthemistery.data.local.entity.AchievementEntity
import com.example.codeofthemistery.data.local.entity.ItemEntity
import com.example.codeofthemistery.data.local.entity.RedeemedVoucherEntity
import com.example.codeofthemistery.data.local.entity.RewardCatalogEntity
import com.example.codeofthemistery.data.local.entity.UserAchievementEntity
import com.example.codeofthemistery.data.local.entity.UserClueEntity
import com.example.codeofthemistery.data.local.entity.UserItemEntity
import com.example.codeofthemistery.data.local.seed.AppSeedData
import com.example.codeofthemistery.data.local.seed.CatalogSeeder
import com.example.codeofthemistery.data.repository.InventoryRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InventorySlotSwapTest {

    private fun createRepo(initialItems: List<UserItemEntity>): Pair<InventoryRepositoryImpl, FakeInventoryDao> {
        val inventoryDao = FakeInventoryDao(initialItems)
        val seeder = CatalogSeeder(inventoryDao, FakeAchievementDao(), FakeRedeemDao())
        return InventoryRepositoryImpl(inventoryDao, seeder) to inventoryDao
    }

    @Test
    fun `equipItemToSlot swaps item in occupied mutable slot and clears previous occupant`() = runTest {
        val uid = "user_1"
        val (repo, dao) = createRepo(
            listOf(
                UserItemEntity(uid, "item_laptop", quantity = 1, equippedSlot = 1, isUnlocked = true),
                UserItemEntity(uid, "item_firewall_shield", quantity = 1, equippedSlot = 2, isUnlocked = true),
                UserItemEntity(uid, "item_syntax_potion", quantity = 2, equippedSlot = 3, isUnlocked = true),
                UserItemEntity(uid, "item_debugger_visor", quantity = 1, equippedSlot = null, isUnlocked = true)
            )
        )

        val fullError = repo.equipItem(uid, "item_debugger_visor")
        assertTrue(fullError is AppResult.Error)

        val swapResult = repo.equipItemToSlot(uid, "item_debugger_visor", targetSlot = 2)
        assertTrue(swapResult is AppResult.Success)
        assertEquals("Firewall Shield", (swapResult as AppResult.Success).data)

        val items = dao.getUserItems(uid).associateBy { it.itemId }
        assertNull(items["item_firewall_shield"]?.equippedSlot)
        assertEquals(2, items["item_debugger_visor"]?.equippedSlot)
        assertEquals(1, items["item_laptop"]?.equippedSlot)
        assertEquals(3, items["item_syntax_potion"]?.equippedSlot)
    }

    @Test
    fun `equipItemToSlot rejects overwriting non-unequippable default item in slot 1`() = runTest {
        val uid = "user_1"
        val (repo, dao) = createRepo(
            listOf(
                UserItemEntity(uid, "item_laptop", quantity = 1, equippedSlot = 1, isUnlocked = true),
                UserItemEntity(uid, "item_firewall_shield", quantity = 1, equippedSlot = 2, isUnlocked = true),
                UserItemEntity(uid, "item_syntax_potion", quantity = 1, equippedSlot = 3, isUnlocked = true),
                UserItemEntity(uid, "item_debugger_visor", quantity = 1, equippedSlot = null, isUnlocked = true)
            )
        )

        val result = repo.equipItemToSlot(uid, "item_debugger_visor", targetSlot = 1)
        assertTrue(result is AppResult.Error)

        val items = dao.getUserItems(uid).associateBy { it.itemId }
        assertEquals(1, items["item_laptop"]?.equippedSlot)
        assertNull(items["item_debugger_visor"]?.equippedSlot)
    }

    @Test
    fun `equipItemToSlot rejects locked items`() = runTest {
        val uid = "user_1"
        val (repo, _) = createRepo(AppSeedData.defaultItemsForNewUser(uid))

        val result = repo.equipItemToSlot(uid, "item_overclock_core", targetSlot = 2)
        assertTrue(result is AppResult.Error)
    }

    private class FakeInventoryDao(initial: List<UserItemEntity>) : InventoryDao() {
        private val catalog = mutableListOf<ItemEntity>()
        private val userItems = initial.toMutableList()

        override fun observeUserInventory(userId: String): Flow<List<ItemJoinedRow>> = flowOf(emptyList())
        override suspend fun getUserItems(userId: String): List<UserItemEntity> =
            userItems.filter { it.userId == userId }

        override suspend fun getUserItem(userId: String, itemId: String): UserItemEntity? =
            userItems.find { it.userId == userId && it.itemId == itemId }

        override suspend fun getAllCatalogItems(): List<ItemEntity> = catalog.toList()

        override suspend fun upsertCatalogItems(items: List<ItemEntity>) {
            catalog.clear()
            catalog.addAll(items)
        }

        override suspend fun upsertUserItems(items: List<UserItemEntity>) {
            items.forEach { upsertUserItem(it) }
        }

        override suspend fun upsertUserItem(item: UserItemEntity) {
            userItems.removeAll { it.userId == item.userId && it.itemId == item.itemId }
            userItems.add(item)
        }

        override suspend fun setEquippedSlot(userId: String, itemId: String, slot: Int?) {
            val idx = userItems.indexOfFirst { it.userId == userId && it.itemId == itemId }
            if (idx >= 0) {
                userItems[idx] = userItems[idx].copy(equippedSlot = slot)
            }
        }

        override suspend fun addQuantity(userId: String, itemId: String, delta: Int) {
            val idx = userItems.indexOfFirst { it.userId == userId && it.itemId == itemId }
            if (idx >= 0) {
                val cur = userItems[idx]
                userItems[idx] = cur.copy(quantity = cur.quantity + delta, isUnlocked = true)
            }
        }
    }

    private class FakeAchievementDao : AchievementDao {
        override fun observeAchievements(userId: String): Flow<List<AchievementJoinedRow>> = flowOf(emptyList())
        override fun observeClues(userId: String): Flow<List<UserClueEntity>> = flowOf(emptyList())
        override suspend fun upsertCatalog(items: List<AchievementEntity>) = Unit
        override suspend fun upsertUserAchievements(rows: List<UserAchievementEntity>) = Unit
        override suspend fun insertClueIfNew(clue: UserClueEntity) = Unit
        override suspend fun deleteCluesForUser(userId: String) = Unit
    }

    private class FakeRedeemDao : RedeemDao() {
        override fun observeCatalog(): Flow<List<RewardCatalogEntity>> = flowOf(emptyList())
        override suspend fun getRewardById(rewardId: String): RewardCatalogEntity? = null
        override fun observeVoucherHistory(userId: String): Flow<List<RedeemedVoucherEntity>> = flowOf(emptyList())
        override suspend fun insertCatalogIfAbsent(items: List<RewardCatalogEntity>) = Unit
        override suspend fun decrementStockIfLimited(rewardId: String): Int = 1
        override suspend fun insertVoucher(voucher: RedeemedVoucherEntity) = Unit
        override suspend fun deleteVouchersForUser(userId: String) = Unit
    }
}
