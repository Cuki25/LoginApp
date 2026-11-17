package com.example.loginapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.theme.FieldShape
import com.example.loginapp.ui.theme.MedKitLine
import com.example.loginapp.ui.theme.MedKitPrimary

/**
 * Primary full-width button.
 *
 * - Background:  #0D5C4A
 * - Text:        white, SemiBold 15sp
 * - Corner:      12.dp
 * - Height:      52.dp
 * - Loading:     shows a white CircularProgressIndicator + disabled state
 */
@Composable
fun MedKitButton(
    text:      String,
    onClick:   () -> Unit,
    modifier:  Modifier = Modifier,
    enabled:   Boolean  = true,
    loading:   Boolean  = false,
    loadingText: String = text
) {
    Button(
        onClick  = { if (!loading) onClick() },
        enabled  = enabled && !loading,
        shape    = FieldShape,
        colors   = ButtonDefaults.buttonColors(
            containerColor         = MedKitPrimary,
            contentColor           = Color.White,
            disabledContainerColor = MedKitPrimary.copy(alpha = 0.5f),
            disabledContentColor   = Color.White.copy(alpha = 0.6f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        if (loading) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color       = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(loadingText, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.7f))
            }
        } else {
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                color = Color.White)
        }
    }
}

/**
 * Outline variant — transparent background with a #0D5C4A border.
 *
 * Used for secondary actions like "Use demo student account" and OAuth buttons.
 */
@Composable
fun MedKitOutlineButton(
    text:     String,
    onClick:  () -> Unit,
    modifier: Modifier = Modifier,
    enabled:  Boolean  = true,
    leadingContent: @Composable (() -> Unit)? = null
) {
    OutlinedButton(
        onClick  = onClick,
        enabled  = enabled,
        shape    = FieldShape,
        border   = BorderStroke(1.5.dp, if (enabled) MedKitPrimary else MedKitLine),
        colors   = ButtonDefaults.outlinedButtonColors(
            contentColor         = MedKitPrimary,
            disabledContentColor = MedKitLine
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        if (leadingContent != null) {
            leadingContent()
            Spacer(Modifier.width(10.dp))
        }
        Text(text, fontWeight = FontWeight.Medium, fontSize = 15.sp)
    }
}
