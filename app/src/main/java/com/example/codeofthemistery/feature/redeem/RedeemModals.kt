package com.example.codeofthemistery.feature.redeem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.BorderHighlight
import com.example.codeofthemistery.core.theme.BorderMuted
import com.example.codeofthemistery.core.theme.CrimsonDanger
import com.example.codeofthemistery.core.theme.OnPrimaryDark
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SuccessGreen
import com.example.codeofthemistery.core.theme.SurfaceCard
import com.example.codeofthemistery.core.theme.SurfaceElevated
import com.example.codeofthemistery.core.theme.TextMuted
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.core.ui.CyberCard
import com.example.codeofthemistery.core.ui.TerminalBadge
import com.example.codeofthemistery.domain.model.RedeemedVoucher
import com.example.codeofthemistery.domain.model.RewardCatalogItem
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RedeemConfirmModal(
    reward: RewardCatalogItem,
    currentPoints: Int,
    isProcessing: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val fmt = NumberFormat.getNumberInstance(Locale.US)
    val remaining = (currentPoints - reward.costPoints).coerceAtLeast(0)

    Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceCard,
            border = BorderStroke(1.dp, BorderHighlight)
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text(
                    text = "Tukar",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PrimaryLavender
                )
                Text(
                    text = "${reward.title}?",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderMuted)
                Spacer(modifier = Modifier.height(14.dp))

                PointSummaryRow("Poin Anda", fmt.format(currentPoints))
                Spacer(modifier = Modifier.height(8.dp))
                PointSummaryRow("Harga", fmt.format(reward.costPoints))
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = BorderMuted)
                Spacer(modifier = Modifier.height(8.dp))
                PointSummaryRow("Sisa", fmt.format(remaining), highlight = true)

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isProcessing,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderHighlight),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                    ) {
                        Text("Batal")
                    }
                    Button(
                        onClick = onConfirm,
                        enabled = !isProcessing,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryLavender,
                            contentColor = OnPrimaryDark
                        )
                    ) {
                        Text(if (isProcessing) "Proses..." else "Tukar", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tidak dapat di refund",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun PointSummaryRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = if (highlight) PrimaryLavender else TextPrimary
        )
    }
}

@Composable
fun RedeemSuccessModal(
    voucher: RedeemedVoucher,
    onDismiss: () -> Unit,
    onCopied: () -> Unit
) {
    val clipboard = LocalClipboardManager.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceCard,
            border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("Berhasil", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Silahkan cek email Anda atau salin kode di bawah",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(BackgroundPrimary)
                        .border(1.dp, PrimaryLavender.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = voucher.voucherCode,
                        style = MaterialTheme.typography.titleSmall,
                        color = PrimaryLavender
                    )
                    IconButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(voucher.voucherCode))
                            onCopied()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Rounded.ContentCopy, contentDescription = "Salin", tint = PrimaryLavender)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryLavender,
                        contentColor = OnPrimaryDark
                    )
                ) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun InsufficientPointsModal(
    message: String = "Maaf, poin Anda tidak cukup",
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = SurfaceCard,
            border = BorderStroke(1.dp, CrimsonDanger.copy(alpha = 0.7f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Warning, contentDescription = null, tint = CrimsonDanger)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherHistorySheet(
    history: List<RedeemedVoucher>,
    onDismiss: () -> Unit,
    onCopied: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val clipboard = LocalClipboardManager.current
    val dateFmt = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceCard,
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text("Riwayat Voucher", style = MaterialTheme.typography.titleLarge, color = PrimaryLavender)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Daftar voucher digital yang telah berhasil Anda tukarkan.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(14.dp))

            if (history.isEmpty()) {
                CyberCard(containerColor = BackgroundPrimary) {
                    Text(
                        text = "Belum ada voucher yang ditukarkan. Kumpulkan poin dari Chapter 1–3 untuk menukar hadiah!",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(history, key = { it.id }) { item ->
                        CyberCard(containerColor = BackgroundPrimary, borderColor = BorderHighlight) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.rewardTitle, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                TerminalBadge(text = "-${item.costPaid} Pts", color = PrimaryLavender)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceElevated)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.voucherCode, style = MaterialTheme.typography.bodySmall, color = SuccessGreen)
                                IconButton(
                                    onClick = {
                                        clipboard.setText(AnnotatedString(item.voucherCode))
                                        onCopied()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Rounded.ContentCopy, contentDescription = "Salin", tint = PrimaryLavender)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dateFmt.format(Date(item.redeemedAt)),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
