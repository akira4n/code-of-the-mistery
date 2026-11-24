package com.example.codeofthemistery.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codeofthemistery.core.theme.BackgroundPrimary
import com.example.codeofthemistery.core.theme.CrimsonDanger
import com.example.codeofthemistery.core.theme.CyanTerminal
import com.example.codeofthemistery.core.theme.PrimaryLavender
import com.example.codeofthemistery.core.theme.TextMuted
import com.example.codeofthemistery.core.theme.TextPrimary
import com.example.codeofthemistery.core.ui.CyberCard
import com.example.codeofthemistery.core.ui.CyberTextField
import com.example.codeofthemistery.core.ui.NeonButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    onResetComplete: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var email by rememberSaveable { mutableStateOf("") }
    var otp by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.resetForgotFlow()
                    onBackToLogin()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.ChevronLeft, contentDescription = "Kembali", tint = PrimaryLavender)
            Text("Back", color = PrimaryLavender, style = MaterialTheme.typography.titleSmall)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Code of the Mistery",
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryLavender
        )
        Spacer(modifier = Modifier.height(30.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Setel Ulang\nPassword Anda",
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when (state.forgotStep) {
                    1 -> "Dapatkan kode OTP anda dengan memasukkan email yang terdaftar"
                    2 -> "Masukkan kode OTP yang telah dikirimkan melalui email"
                    else -> "Buat password baru untuk akun terminal Anda"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (state.forgotStep) {
            1 -> {
                CyberTextField(
                    label = "Konfirmasi Email",
                    value = email,
                    onValueChange = { email = it; viewModel.clearMessages() },
                    placeholder = "Masukkan email anda",
                    keyboardType = KeyboardType.Email
                )
                Spacer(modifier = Modifier.height(22.dp))
                NeonButton(
                    text = "Confirm",
                    enabled = !state.isLoading,
                    onClick = { viewModel.requestResetOtp(email) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            2 -> {
                state.simulatedOtpHint?.let { code ->
                    CyberCard(borderColor = CyanTerminal.copy(alpha = 0.5f)) {
                        Text(
                            text = "SIMULASI EMAIL OTP: $code",
                            color = CyanTerminal,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Dikirim ke ${state.resetEmail} (mode offline UAS)",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                CyberTextField(
                    label = "Kode OTP",
                    value = otp,
                    onValueChange = { if (it.length <= 6) otp = it; viewModel.clearMessages() },
                    placeholder = "Masukkan 6 digit kode OTP",
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(22.dp))
                NeonButton(
                    text = "Confirm",
                    onClick = { viewModel.verifyOtpAndProceed(otp) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            else -> {
                CyberTextField(
                    label = "Password Baru",
                    value = newPassword,
                    onValueChange = { newPassword = it; viewModel.clearMessages() },
                    placeholder = "Minimal 6 karakter",
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(14.dp))
                CyberTextField(
                    label = "Konfirmasi Password Baru",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; viewModel.clearMessages() },
                    placeholder = "Masukkan ulang password baru",
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(22.dp))
                NeonButton(
                    text = "Simpan Password",
                    enabled = !state.isLoading,
                    onClick = { viewModel.submitNewPassword(otp, newPassword, confirmPassword, onResetComplete) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        state.errorMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, color = CrimsonDanger, style = MaterialTheme.typography.bodySmall)
        }
    }
}
