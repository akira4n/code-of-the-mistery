package com.example.codeofthemistery.feature.redeem

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codeofthemistery.R
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.BorderHighlight
import com.example.codeofthemistery.core.theme.BorderMuted
import com.example.codeofthemistery.core.theme.CyanTerminal
import com.example.codeofthemistery.core.theme.OnPrimaryDark
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SurfaceCard
import com.example.codeofthemistery.core.theme.SurfaceElevated
import com.example.codeofthemistery.core.theme.TextMuted
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.core.ui.CyberCard
import com.example.codeofthemistery.core.ui.TerminalBadge
import com.example.codeofthemistery.core.ui.TerminalTopBar
import com.example.codeofthemistery.domain.model.RewardCatalogItem
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun RedeemScreen(
    viewModel: RedeemViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val fmt = NumberFormat.getNumberInstance(Locale.US)
    var showHistorySheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TerminalTopBar(title = "Redeem")

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "Tukar poin yang telah kamu kumpulkan dengan aset real-life, pilih secara bijak ya!",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }

                item {
                    CyberCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BackgroundPrimary)
                                    .border(1.dp, BorderHighlight, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = PrimaryLavender
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("POIN KAMU", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = fmt.format(state.userPoints),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Points",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PrimaryLavender
                                    )
                                }
                            }
                            OutlinedButton(
                                onClick = { showHistorySheet = true },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryLavender)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Riwayat (${state.history.size})", style = MaterialTheme.typography.labelSmall, color = PrimaryLavender)
                            }
                        }
                    }
                }

                item { HorizontalDivider(color = BorderMuted) }

                items(state.catalog, key = { it.id }) { reward ->
                    RewardCard(
                        reward = reward,
                        fmt = fmt,
                        onRedeemClick = { viewModel.onClickRedeem(reward) }
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    when (val dialog = state.dialogState) {
        is RedeemDialogState.Confirm -> {
            RedeemConfirmModal(
                reward = dialog.reward,
                currentPoints = dialog.currentPoints,
                isProcessing = state.isProcessing,
                onDismiss = viewModel::dismissDialog,
                onConfirm = { viewModel.confirmRedeem(dialog.reward) }
            )
        }
        is RedeemDialogState.Success -> {
            RedeemSuccessModal(
                voucher = dialog.voucher,
                onDismiss = viewModel::dismissDialog,
                onCopied = {
                    scope.launch { snackbarHostState.showSnackbar("Kode voucher disalin ke clipboard") }
                }
            )
        }
        RedeemDialogState.InsufficientPoints -> {
            InsufficientPointsModal(onDismiss = viewModel::dismissDialog)
        }
        is RedeemDialogState.Error -> {
            InsufficientPointsModal(message = dialog.message, onDismiss = viewModel::dismissDialog)
        }
        RedeemDialogState.None -> Unit
    }

    if (showHistorySheet) {
        VoucherHistorySheet(
            history = state.history,
            onDismiss = { showHistorySheet = false },
            onCopied = {
                scope.launch { snackbarHostState.showSnackbar("Kode voucher disalin ke clipboard") }
            }
        )
    }
}

@Composable
private fun RewardCard(
    reward: RewardCatalogItem,
    fmt: NumberFormat,
    onRedeemClick: () -> Unit
) {
    val outOfStock = reward.stockRemaining != null && reward.stockRemaining <= 0

    CyberCard(contentPadding = PaddingValues(0.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(106.dp)
                .background(SurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            when (reward.partner) {
                "shopee" -> Image(
                    painter = painterResource(R.drawable.logo_shopee),
                    contentDescription = reward.title,
                    modifier = Modifier.height(68.dp),
                    contentScale = ContentScale.Fit
                )
                "tokopedia" -> Image(
                    painter = painterResource(R.drawable.logo_tokopedia),
                    contentDescription = reward.title,
                    modifier = Modifier.height(42.dp).padding(horizontal = 24.dp),
                    contentScale = ContentScale.Fit
                )
                "dana" -> Image(
                    painter = painterResource(R.drawable.logo_dana),
                    contentDescription = reward.title,
                    modifier = Modifier.height(42.dp).padding(horizontal = 24.dp),
                    contentScale = ContentScale.Fit
                )
                "copilot" -> Icon(
                    imageVector = Icons.Rounded.Code,
                    contentDescription = reward.title,
                    tint = CyanTerminal,
                    modifier = Modifier.size(48.dp)
                )
                else -> Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = reward.title,
                    tint = PrimaryLavender,
                    modifier = Modifier.size(48.dp)
                )
            }

            reward.stockRemaining?.let { stock ->
                TerminalBadge(
                    text = if (stock > 0) "Stok tersisa $stock" else "Stok Habis",
                    color = if (stock > 0) PrimaryLavender else Color.Gray,
                    backgroundColor = BackgroundPrimary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(reward.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(reward.description, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("COST", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(
                        text = "${fmt.format(reward.costPoints)} Points",
                        style = MaterialTheme.typography.titleSmall,
                        color = PrimaryLavender
                    )
                }
                Button(
                    onClick = onRedeemClick,
                    enabled = !outOfStock,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryLavender,
                        contentColor = OnPrimaryDark,
                        disabledContainerColor = SurfaceElevated,
                        disabledContentColor = TextMuted
                    )
                ) {
                    Text(if (outOfStock) "Habis" else "Tukar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
