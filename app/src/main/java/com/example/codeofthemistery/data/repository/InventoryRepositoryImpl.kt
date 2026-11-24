package com.example.codeofthemistery.data.repository

import com.example.codeofthemistery.core.util.AppResult
import com.example.codeofthemistery.data.local.dao.InventoryDao
import com.example.codeofthemistery.data.local.dao.ItemJoinedRow
import com.example.codeofthemistery.data.local.entity.UserItemEntity
import com.example.codeofthemistery.data.local.seed.CatalogSeeder
import com.example.codeofthemistery.domain.model.ItemRarity
import com.example.codeofthemistery.domain.model.ItemType
import com.example.codeofthemistery.domain.model.OwnedItem
import com.example.codeofthemistery.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InventoryRepositoryImpl(
    private val inventoryDao: InventoryDao,
    private val catalogSeeder: CatalogSeeder
) : InventoryRepository {

    override fun observeInventory(userId: String): Flow<List<OwnedItem>> =
        inventoryDao.observeUserInventory(userId).map { rows ->
            rows.map { it.toDomain() }
        }

    override suspend fun equipItem(userId: String, itemId: String): AppResult<Int> {
        catalogSeeder.ensureSeeded()
        val items = inventoryDao.getUserItems(userId)
        val target = items.find { it.itemId == itemId }
            ?: return AppResult.Error("Item tidak ditemukan.")
        if (!target.isUnlocked || target.quantity <= 0) {
            return AppResult.Error("Item masih terkunci atau habis.")
        }
        if (target.equippedSlot != null) {
            return AppResult.Success(target.equippedSlot)
        }
        val usedSlots = items.mapNotNull { it.equippedSlot }.toSet()
        val freeSlot = listOf(2, 3).firstOrNull { it !in usedSlots }
            ?: return AppResult.Error("Slot equipment penuh (maksimal 3 slot). Lepas 1 item terlebih dahulu.")
        inventoryDao.setEquippedSlot(userId, itemId, freeSlot)
        return AppResult.Success(freeSlot)
    }

    override suspend fun unequipItem(userId: String, itemId: String): AppResult<Unit> {
        val catalogItem = inventoryDao.getAllCatalogItems().find { it.id == itemId }
            ?: return AppResult.Error("Item tidak ditemukan.")
        if (!catalogItem.canUnequip) {
            return AppResult.Error("${catalogItem.name} adalah item default dan tidak bisa dilepas.")
        }
        inventoryDao.setEquippedSlot(userId, itemId, null)
        return AppResult.Success(Unit)
    }

    override suspend fun consumeItem(userId: String, itemId: String): AppResult<Unit> {
        val current = inventoryDao.getUserItem(userId, itemId)
            ?: return AppResult.Error("Item tidak dimiliki.")
        if (!current.isUnlocked || current.quantity <= 0) {
            return AppResult.Error("Stok item habis.")
        }
        val nextQty = current.quantity - 1
        inventoryDao.upsertUserItem(
            current.copy(
                quantity = nextQty,
                equippedSlot = if (nextQty == 0) null else current.equippedSlot
            )
        )
        return AppResult.Success(Unit)
    }

    override suspend fun grantLootByHp(userId: String, hpRemaining: Int): OwnedItem? {
        catalogSeeder.ensureSeeded()
        val userItems = inventoryDao.getUserItems(userId).associateBy { it.itemId }
        val targetItemId = when {
            hpRemaining >= 6 && userItems["item_overclock_core"]?.isUnlocked != true -> "item_overclock_core"
            hpRemaining >= 4 && userItems["item_debugger_visor"]?.isUnlocked != true -> "item_debugger_visor"
            userItems["item_firewall_shield"]?.isUnlocked != true -> "item_firewall_shield"
            else -> "item_syntax_potion"
        }

        val existing = userItems[targetItemId]
        if (existing == null) {
            inventoryDao.upsertUserItem(
                UserItemEntity(userId = userId, itemId = targetItemId, quantity = 1, equippedSlot = null, isUnlocked = true)
            )
        } else {
            inventoryDao.addQuantity(userId, targetItemId, 1)
        }

        val catalog = inventoryDao.getAllCatalogItems().find { it.id == targetItemId } ?: return null
        return OwnedItem(
            itemId = catalog.id,
            name = catalog.name,
            description = catalog.description,
            statLabel = catalog.statLabel,
            type = ItemType.valueOf(catalog.type),
            rarity = ItemRarity.valueOf(catalog.rarity),
            quantity = (existing?.quantity ?: 0) + 1,
            equippedSlot = existing?.equippedSlot,
            canUnequip = catalog.canUnequip,
            isUnlocked = true
        )
    }

    private fun ItemJoinedRow.toDomain(): OwnedItem = OwnedItem(
        itemId = id,
        name = name,
        description = description,
        statLabel = statLabel,
        type = runCatching { ItemType.valueOf(type) }.getOrDefault(ItemType.EQUIPMENT),
        rarity = runCatching { ItemRarity.valueOf(rarity) }.getOrDefault(ItemRarity.COMMON),
        quantity = quantity ?: 0,
        equippedSlot = equippedSlot,
        canUnequip = canUnequip,
        isUnlocked = isUnlocked ?: false
    )
}
