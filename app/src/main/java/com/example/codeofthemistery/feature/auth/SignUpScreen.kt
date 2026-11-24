package com.example.codeofthemistery.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.CrimsonDanger
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SurfaceCard
import com.example.codeofthemistery.core.theme.TextMuted
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.core.ui.CyberTextField
import com.example.codeofthemistery.core.ui.NeonButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var oauthProviderDialog by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Code of the Mistery",
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryLavender
        )

        Spacer(modifier = Modifier.height(26.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Inisialisasi Identitas\nBaru Anda",
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Mari memulai pertualangan anda dengan menggunakan terminal!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        CyberTextField(
            label = "Nama",
            value = name,
            onValueChange = { name = it; viewModel.clearMessages() },
            placeholder = "Masukkan nama anda"
        )
        Spacer(modifier = Modifier.height(14.dp))

        CyberTextField(
            label = "Email",
            value = email,
            onValueChange = { email = it; viewModel.clearMessages() },
            placeholder = "Masukkan email anda",
            keyboardType = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(14.dp))

        CyberTextField(
            label = "Password",
            value = password,
            onValueChange = { password = it; viewModel.clearMessages() },
            placeholder = "Masukkan password Anda",
            isPassword = true
        )
        Spacer(modifier = Modifier.height(14.dp))

        CyberTextField(
            label = "Konfirmasi Password",
            value = confirmPassword,
            onValueChange = { confirmPassword = it; viewModel.clearMessages() },
            placeholder = "Masukkan password Anda sekali lagi",
            isPassword = true
        )

        state.errorMessage?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = it, color = CrimsonDanger, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(22.dp))

        NeonButton(
            text = if (state.isLoading) "Mendaftarkan..." else "Sign Up",
            enabled = !state.isLoading,
            onClick = { viewModel.register(name, email, password, confirmPassword, onSignUpSuccess) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Text("Sudah punya akun? ", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryLavender,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("or", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                SocialPlaceholderRow(
                    onGoogleClick = { oauthProviderDialog = "Google" },
                    onFacebookClick = { oauthProviderDialog = "Facebook" }
                )
            }
        }
    }

    oauthProviderDialog?.let { provider ->
        AlertDialog(
            onDismissRequest = { oauthProviderDialog = null },
            containerColor = SurfaceCard,
            title = { Text("OAuth $provider (Phase 2)", color = PrimaryLavender) },
            text = {
                Text(
                    "Registrasi via $provider disiapkan untuk backend Express + JWT. Silakan isi form Sign Up di atas untuk akun lokal.",
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            confirmButton = {
                TextButton(onClick = { oauthProviderDialog = null }) {
                    Text("Tutup", color = PrimaryLavender)
                }
            }
        )
    }
}
