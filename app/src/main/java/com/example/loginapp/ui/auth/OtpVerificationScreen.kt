package com.example.loginapp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.data.AuthSuccessData
import com.example.loginapp.ui.components.BrandMark
import com.example.loginapp.ui.components.MedKitButton
import com.example.loginapp.ui.theme.MedKitBackground
import com.example.loginapp.ui.theme.MedKitEmergencyRed
import com.example.loginapp.ui.theme.MedKitInkFaint
import com.example.loginapp.ui.theme.MedKitInkMain
import com.example.loginapp.ui.theme.MedKitInkSoft
import com.example.loginapp.ui.theme.MedKitLine
import com.example.loginapp.ui.theme.MedKitPrimary
import com.example.loginapp.ui.theme.MedKitPrimaryTint
import com.example.loginapp.ui.theme.MedKitSurface

@Composable
fun OtpVerificationScreen(
    viewModel:       AuthViewModel,
    email:           String,
    password:        String,
    studentNumber:   String,          // collected on RegisterScreen
    onVerified:      (AuthSuccessData) -> Unit,
    onResend:        () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var code by remember { mutableStateOf("") }

    // When OTP is verified, the ViewModel immediately calls completeRegistration.
    // Watch for the final authSuccess to fire navigation.
    LaunchedEffect(uiState.otpVerified) {
        if (uiState.otpVerified) {
            viewModel.completeRegistration(email, password, studentNumber)
        }
    }
    LaunchedEffect(uiState.authSuccess) {
        uiState.authSuccess?.let { data ->
            viewModel.clearAuthSuccess()
            onVerified(data)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MedKitBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BrandMark(modifier = Modifier.align(Alignment.Start))

        Spacer(Modifier.height(36.dp))

        // ── Email icon badge ──────────────────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MedKitPrimaryTint)
        ) {
            Icon(
                imageVector        = Icons.Filled.Email,
                contentDescription = null,
                tint               = MedKitPrimary,
                modifier           = Modifier.size(32.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text       = "Verify your email",
            fontSize   = 26.sp,
            fontWeight = FontWeight.Bold,
            color      = MedKitInkMain,
            textAlign  = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text      = "We sent a 6-digit code to",
            style     = MaterialTheme.typography.bodyMedium,
            color     = MedKitInkSoft,
            textAlign = TextAlign.Center
        )
        Text(
            text       = email,
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color      = MedKitPrimary,
            textAlign  = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        // ── 6-cell OTP input ──────────────────────────────────────────────────
        OtpInputRow(
            code      = code,
            onCodeChange = { newCode ->
                if (newCode.length <= 6 && newCode.all { it.isDigit() }) {
                    code = newCode
                    viewModel.clearError()
                }
            }
        )

        // ── Error message ─────────────────────────────────────────────────────
        uiState.errorMessage?.let { msg ->
            Spacer(Modifier.height(12.dp))
            Text(
                text      = msg,
                color     = MedKitEmergencyRed,
                fontSize  = 13.sp,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(28.dp))

        // ── Verify & Complete Registration button ─────────────────────────────
        MedKitButton(
            text        = "Verify & Complete Registration",
            loadingText = "Verifying…",
            loading     = uiState.isLoading,
            enabled     = code.length == 6,
            onClick     = { viewModel.verifyOtp(email, code) }
        )

        Spacer(Modifier.height(16.dp))

        // ── Spam folder hint ──────────────────────────────────────────────────
        Spacer(Modifier.height(8.dp))
        Text(
            "Didn't receive the email? Check your spam folder or server logs during testing.",
            style     = MaterialTheme.typography.bodySmall,
            color     = MedKitInkFaint,
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(4.dp))

        // ── Resend link ───────────────────────────────────────────────────────
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier              = Modifier.fillMaxWidth()
        ) {
            Text(
                "Didn't receive a code? ",
                style = MaterialTheme.typography.bodySmall,
                color = MedKitInkFaint
            )
            TextButton(
                onClick = onResend,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Text("Resend", color = MedKitPrimary,
                    fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ── 6-cell OTP row ────────────────────────────────────────────────────────────

@Composable
private fun OtpInputRow(
    code:         String,
    onCodeChange: (String) -> Unit
) {
    // Hidden BasicTextField that holds the actual text; the cells are drawn on top.
    Box(contentAlignment = Alignment.Center) {
        BasicTextField(
            value           = code,
            onValueChange   = onCodeChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            cursorBrush     = SolidColor(Color.Transparent),
            decorationBox   = { _ ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier              = Modifier.fillMaxWidth()
                ) {
                    repeat(6) { index ->
                        OtpCell(
                            char      = code.getOrNull(index),
                            isFocused = index == code.length
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun OtpCell(char: Char?, isFocused: Boolean) {
    val borderColor = when {
        isFocused -> MedKitPrimary
        char != null -> MedKitPrimary.copy(alpha = 0.5f)
        else -> MedKitLine
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(width = 46.dp, height = 56.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MedKitSurface)
            .border(
                width = if (isFocused) 2.dp else 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Text(
            text       = char?.toString() ?: "",
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold,
            color      = MedKitInkMain,
            textAlign  = TextAlign.Center
        )
    }
}
