package com.example.codeofthemistery.domain.model

enum class ItemType {
    EQUIPMENT,
    CONSUMABLE
}

enum class ItemRarity {
    COMMON,
    RARE,
    EPIC
}

data class OwnedItem(
    val itemId: String,
    val name: String,
    val description: String,
    val statLabel: String,
    val type: ItemType,
    val rarity: ItemRarity,
    val quantity: Int,
    val equippedSlot: Int?, // 1, 2, 3 or null
    val canUnequip: Boolean,
    val isUnlocked: Boolean
)
