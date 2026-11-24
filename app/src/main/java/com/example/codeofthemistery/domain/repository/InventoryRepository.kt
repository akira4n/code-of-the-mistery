package com.example.codeofthemistery.domain.repository

import com.example.codeofthemistery.core.util.AppResult
import com.example.codeofthemistery.domain.model.ItemRarity
import com.example.codeofthemistery.domain.model.OwnedItem
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    fun observeInventory(userId: String): Flow<List<OwnedItem>>
    suspend fun equipItem(userId: String, itemId: String): AppResult<Int>
    suspend fun unequipItem(userId: String, itemId: String): AppResult<Unit>
    suspend fun consumeItem(userId: String, itemId: String): AppResult<Unit>
    suspend fun grantLootByHp(userId: String, hpRemaining: Int): OwnedItem?
}
