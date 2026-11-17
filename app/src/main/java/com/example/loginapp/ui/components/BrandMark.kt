package com.example.loginapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.theme.CrestShape
import com.example.loginapp.ui.theme.MedKitInkFaint
import com.example.loginapp.ui.theme.MedKitInkMain
import com.example.loginapp.ui.theme.MedKitPrimary

/**
 * MedKit brand lockup.
 *
 * Displays a 44×44 dp emerald-green rounded crest containing a white
 * hospital cross, next to the app name and tagline.
 *
 *   ┌────────────────────────┐
 *   │  [✚]  MedKit           │
 *   │       Taguig Campus…   │
 *   └────────────────────────┘
 */
@Composable
fun BrandMark(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // ── Crest ──────────────────────────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .background(color = MedKitPrimary, shape = CrestShape)
        ) {
            Icon(
                imageVector       = Icons.Filled.LocalHospital,
                contentDescription = "MedKit crest",
                tint              = Color.White,
                modifier          = Modifier.size(26.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        // ── Wordmark ────────────────────────────────────────────────────────
        Column {
            Text(
                text       = "MedKit",
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = MedKitInkMain,
                lineHeight = 22.sp
            )
            Text(
                text       = "Taguig Campus Emergency Response",
                fontSize   = 11.sp,
                fontWeight = FontWeight.Normal,
                color      = MedKitInkFaint,
                lineHeight = 14.sp
            )
        }
    }
}
