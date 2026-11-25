package com.example.codeofthemistery.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.CrimsonDanger
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.SuccessGreen
import com.example.codeofthemistery.core.theme.SurfaceCard
import com.example.codeofthemistery.core.theme.TextMuted
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.core.ui.CyberTextField
import com.example.codeofthemistery.core.ui.NeonButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgot: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var oauthProviderDialog by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Code of the Mystery",
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryLavender
        )

        Spacer(modifier = Modifier.height(36.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Kembali\nBerpetualang!",
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Autentikasi terminal anda untuk melanjutkan perjalanan.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        CyberTextField(
            label = "Email",
            value = email,
            onValueChange = { email = it; viewModel.clearMessages() },
            placeholder = "Masukkan email anda",
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(18.dp))

        CyberTextField(
            label = "Password",
            value = password,
            onValueChange = { password = it; viewModel.clearMessages() },
            placeholder = "Masukkan password Anda",
            isPassword = true,
            trailingHeader = {
                Text(
                    text = "Lupa password?",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimaryLavender,
                    modifier = Modifier.clickable { onNavigateToForgot() }
                )
            }
        )

        state.errorMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, color = CrimsonDanger, style = MaterialTheme.typography.bodySmall)
        }
        state.infoMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, color = SuccessGreen, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        NeonButton(
            text = if (state.isLoading) "Memverifikasi..." else "Login",
            enabled = !state.isLoading,
            onClick = { viewModel.login(email, password, onLoginSuccess) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(22.dp))

        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Belum punya akun? ",
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary
            )
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = PrimaryLavender,
                modifier = Modifier.clickable { onNavigateToSignUp() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "or", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Spacer(modifier = Modifier.height(14.dp))

        SocialPlaceholderRow(
            onGoogleClick = { oauthProviderDialog = "Google" },
            onFacebookClick = { oauthProviderDialog = "Facebook" }
        )
    }

    oauthProviderDialog?.let { provider ->
        AlertDialog(
            onDismissRequest = { oauthProviderDialog = null },
            containerColor = SurfaceCard,
            title = { Text("Login dengan $provider", color = PrimaryLavender) },
            text = {
                Text(
                    "Login melalui akun $provider belum tersedia pada versi saat ini. Silakan gunakan formulir email dan password di atas.",
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            confirmButton = {
                TextButton(onClick = { oauthProviderDialog = null }) {
                    Text("Mengerti", color = PrimaryLavender)
                }
            }
        )
    }
}

@Composable
internal fun SocialPlaceholderRow(
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        Surface(
            onClick = onGoogleClick,
            shape = CircleShape,
            color = Color.White,
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("G", color = Color(0xFFDB4437), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        Surface(
            onClick = onFacebookClick,
            shape = CircleShape,
            color = Color(0xFF1877F2),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("f", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}
