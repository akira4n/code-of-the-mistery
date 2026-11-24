package com.example.codeofthemistery.feature.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeofthemistery.core.util.AppResult
import com.example.codeofthemistery.domain.model.OwnedItem
import com.example.codeofthemistery.domain.repository.AuthRepository
import com.example.codeofthemistery.domain.repository.InventoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ItemsUiState(
    val userId: String? = null,
    val equippedSlots: List<OwnedItem?> = listOf(null, null, null),
    val allItems: List<OwnedItem> = emptyList(),
    val statusMessage: String? = null
)

class ItemsViewModel(
    authRepository: AuthRepository,
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    private val feedbackFlow = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ItemsUiState> = authRepository.observeActiveUser()
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(ItemsUiState())
            } else {
                combine(inventoryRepository.observeInventory(user.id), feedbackFlow) { items, msg ->
                    val slots = listOf(1, 2, 3).map { slot -> items.find { it.equippedSlot == slot } }
                    ItemsUiState(
                        userId = user.id,
                        equippedSlots = slots,
                        allItems = items,
                        statusMessage = msg
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ItemsUiState())

    fun clearMessage() {
        feedbackFlow.value = null
    }

    fun toggleEquip(item: OwnedItem) {
        val uid = uiState.value.userId ?: return
        viewModelScope.launch {
            if (item.equippedSlot != null) {
                when (val res = inventoryRepository.unequipItem(uid, item.itemId)) {
                    is AppResult.Success -> feedbackFlow.value = "${item.name} dilepas dari slot."
                    is AppResult.Error -> feedbackFlow.value = res.message
                }
            } else {
                when (val res = inventoryRepository.equipItem(uid, item.itemId)) {
                    is AppResult.Success -> feedbackFlow.value = "${item.name} dipasang di Slot ${res.data}."
                    is AppResult.Error -> feedbackFlow.value = res.message
                }
            }
        }
    }
}
