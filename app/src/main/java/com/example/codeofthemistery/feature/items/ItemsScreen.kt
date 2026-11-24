package com.example.codeofthemistery.feature.items

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.LaptopMac
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.BorderHighlight
import com.example.codeofthemistery.core.theme.BorderMuted
import com.example.codeofthemistery.core.theme.CrimsonDanger
import com.example.codeofthemistery.core.theme.CyanTerminal
import com.example.codeofthemistery.core.theme.GoldAccent
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SurfaceCard
import com.example.codeofthemistery.core.theme.SurfaceElevated
import com.example.codeofthemistery.core.theme.TextMuted
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.core.theme.TextSecondary
import com.example.codeofthemistery.core.ui.CyberCard
import com.example.codeofthemistery.core.ui.TerminalBadge
import com.example.codeofthemistery.core.ui.TerminalTopBar
import com.example.codeofthemistery.domain.model.ItemRarity
import com.example.codeofthemistery.domain.model.OwnedItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun ItemsScreen(
    viewModel: ItemsViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.statusMessage) {
        state.statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TerminalTopBar(title = "Items")

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Item yang dipakai",
                            style = MaterialTheme.typography.titleMedium,
                            color = PrimaryLavender
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(18.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            state.equippedSlots.forEach { slotItem ->
                                EquippedSlotBox(item = slotItem)
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        HorizontalDivider(color = BorderMuted)
                    }
                }

                item {
                    Text(
                        text = "Item yang tersedia",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                }

                items(state.allItems, key = { it.itemId }) { item ->
                    InventoryItemCard(
                        item = item,
                        onToggleEquip = { viewModel.toggleEquip(item) }
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun EquippedSlotBox(item: OwnedItem?, modifier: Modifier = Modifier) {
    val accent = item?.let { itemAccentColor(it.itemId, it.rarity) } ?: BorderHighlight
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceCard)
                .border(1.2.dp, if (item != null) accent.copy(alpha = 0.7f) else BorderHighlight, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (item != null) {
                Icon(
                    imageVector = itemIcon(item.itemId),
                    contentDescription = item.name,
                    tint = accent,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item?.name ?: "Kosong",
            style = MaterialTheme.typography.labelSmall,
            color = if (item != null) TextSecondary else TextMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InventoryItemCard(
    item: OwnedItem,
    onToggleEquip: () -> Unit
) {
    val accent = itemAccentColor(item.itemId, item.rarity)
    val isEquipped = item.equippedSlot != null
    val unlockedAndAvailable = item.isUnlocked && (item.quantity > 0 || !item.canUnequip)

    CyberCard(
        borderColor = if (!unlockedAndAvailable) BorderMuted else if (isEquipped) accent.copy(alpha = 0.6f) else BorderHighlight
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundPrimary)
                    .border(1.dp, if (unlockedAndAvailable) accent.copy(alpha = 0.55f) else BorderMuted, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (unlockedAndAvailable) itemIcon(item.itemId) else Icons.Rounded.Lock,
                    contentDescription = item.name,
                    tint = if (unlockedAndAvailable) accent else TextMuted,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val statusLabel = when {
                    !item.canUnequip -> "DEFAULT ITEM"
                    !unlockedAndAvailable -> "LOCKED"
                    isEquipped -> "EQUIPPED"
                    else -> "QTY x${item.quantity}"
                }
                val statusColor = when {
                    !item.canUnequip -> GoldAccent
                    !unlockedAndAvailable -> TextMuted
                    isEquipped -> PrimaryLavender
                    else -> CyanTerminal
                }
                TerminalBadge(text = statusLabel, color = statusColor)
                if (unlockedAndAvailable) {
                    TerminalBadge(text = "${item.rarity.name} • ${item.statLabel}", color = accent)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = item.name,
            style = MaterialTheme.typography.titleLarge,
            color = if (unlockedAndAvailable) TextPrimary else TextMuted
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = BorderMuted)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            when {
                !item.canUnequip -> {
                    Text("Can't be Unequip", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                !unlockedAndAvailable -> {
                    Text("Item ini masih terkunci.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                else -> {
                    OutlinedButton(
                        onClick = onToggleEquip,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isEquipped) SurfaceElevated else PrimaryLavender.copy(alpha = 0.14f),
                            contentColor = if (isEquipped) TextSecondary else PrimaryLavender
                        )
                    ) {
                        Text(if (isEquipped) "Unequip" else "Equip")
                    }
                }
            }
        }
    }
}

fun itemIcon(itemId: String): ImageVector = when (itemId) {
    "item_laptop" -> Icons.Rounded.LaptopMac
    "item_firewall_shield" -> Icons.Rounded.Shield
    "item_syntax_potion" -> Icons.Rounded.LocalDrink
    "item_debugger_visor" -> Icons.Rounded.BugReport
    "item_overclock_core" -> Icons.Rounded.AutoAwesome
    else -> Icons.Rounded.Shield
}

fun itemAccentColor(itemId: String, rarity: ItemRarity): Color = when {
    itemId == "item_laptop" -> GoldAccent
    itemId == "item_syntax_potion" -> CrimsonDanger
    rarity == ItemRarity.EPIC -> GoldAccent
    rarity == ItemRarity.RARE -> PrimaryLavender
    else -> CyanTerminal
}
