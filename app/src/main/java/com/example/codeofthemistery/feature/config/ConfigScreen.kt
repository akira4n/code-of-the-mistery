package com.example.codeofthemistery.feature.config

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.BorderMuted
import com.example.codeofthemistery.core.theme.CrimsonDark
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SurfaceCard
import com.example.codeofthemistery.core.theme.TextMuted
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.core.ui.CyberCard
import com.example.codeofthemistery.core.ui.CyberTextField
import com.example.codeofthemistery.core.ui.TerminalTopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun ConfigScreen(
    onLoggedOut: () -> Unit,
    viewModel: ConfigViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var showAccountDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.message) {
        state.message?.let {
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
            TerminalTopBar(title = "Config")

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ConfigMenuRow(
                        title = "Akun",
                        subtitle = state.user?.email ?: "-",
                        icon = Icons.Rounded.Person,
                        onClick = { showAccountDialog = true }
                    )
                }
                item {
                    ConfigMenuRow(
                        title = "Privasi dan Keamanan",
                        subtitle = "Kelola password & keamanan akun",
                        icon = Icons.Rounded.Security,
                        onClick = { showSecurityDialog = true }
                    )
                }
                item {
                    ConfigMenuRow(
                        title = "Tentang dan Bantuan",
                        subtitle = "Informasi platform & aplikasi • v1.0",
                        icon = Icons.Rounded.HelpOutline,
                        onClick = { showAboutDialog = true }
                    )
                }

                item {
                    CyberCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Haptic Feedback", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Text("Getaran umpan balik saat battle & error", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }
                            Switch(
                                checked = state.hapticsEnabled,
                                onCheckedChange = viewModel::toggleHaptics,
                                colors = SwitchDefaults.colors(checkedThumbColor = PrimaryLavender)
                            )
                        }
                    }
                }

                item {
                    HorizontalDivider(color = BorderMuted, modifier = Modifier.padding(vertical = 6.dp))
                    Button(
                        onClick = { viewModel.logout(onLoggedOut) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CrimsonDark,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Rounded.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Logout", fontWeight = FontWeight.Bold)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Code of the Mystery",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }

    if (showAccountDialog) {
        var nameInput by remember(state.user) { mutableStateOf(state.user?.name ?: "") }
        AlertDialog(
            onDismissRequest = { showAccountDialog = false },
            containerColor = SurfaceCard,
            title = { Text("Profil Akun", color = PrimaryLavender) },
            text = {
                Column {
                    Text(
                        text = state.user?.email ?: "-",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CyberTextField(
                        label = "Nama Tampilan",
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        placeholder = "Masukkan nama baru"
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateName(nameInput)
                    showAccountDialog = false
                }) { Text("Simpan", color = PrimaryLavender) }
            },
            dismissButton = {
                TextButton(onClick = { showAccountDialog = false }) { Text("Batal", color = TextMuted) }
            }
        )
    }

    if (showSecurityDialog) {
        var curPw by remember { mutableStateOf("") }
        var newPw by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showSecurityDialog = false },
            containerColor = SurfaceCard,
            title = { Text("Privasi dan Keamanan", color = PrimaryLavender) },
            text = {
                Column {
                    Text(
                        "Data akun dan progres belajar Anda tersimpan dengan aman di perangkat ini. Anda dapat memperbarui password akun melalui formulir di bawah ini.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CyberTextField("Password Saat Ini", curPw, { curPw = it }, "Password lama", isPassword = true)
                    Spacer(modifier = Modifier.height(10.dp))
                    CyberTextField("Password Baru", newPw, { newPw = it }, "Min. 6 karakter", isPassword = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.changePassword(curPw, newPw)
                    showSecurityDialog = false
                }) { Text("Ubah", color = PrimaryLavender) }
            },
            dismissButton = {
                TextButton(onClick = { showSecurityDialog = false }) { Text("Batal", color = TextMuted) }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = SurfaceCard,
            title = { Text("Tentang Code of the Mystery", color = PrimaryLavender) },
            text = {
                Text(
                    "Code of the Mystery adalah platform gamifikasi pembelajaran pemrograman Python berbasis Edu-RPG dan Visual Novel.\n\nPelajari sintaks, logika pemrograman, analisis petunjuk, serta selesaikan tantangan kode secara interaktif di dalam game.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("Tutup", color = PrimaryLavender) }
            }
        )
    }
}

@Composable
private fun ConfigMenuRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    CyberCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(icon, contentDescription = null, tint = PrimaryLavender)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
            }
            Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextMuted)
        }
    }
}
