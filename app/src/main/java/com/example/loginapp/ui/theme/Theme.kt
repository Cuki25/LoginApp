package com.example.loginapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// ── MedKit design-system color scheme (light only — dark mode TBD) ────────────
private val MedKitColorScheme = lightColorScheme(
    primary          = MedKitPrimary,
    onPrimary        = Color.White,
    primaryContainer = MedKitPrimaryTint,
    onPrimaryContainer = MedKitPrimaryDark,

    secondary        = MedKitPrimaryDark,
    onSecondary      = Color.White,

    background       = MedKitBackground,
    onBackground     = MedKitInkMain,

    surface          = MedKitSurface,
    onSurface        = MedKitInkMain,
    surfaceVariant   = MedKitSurfaceSecondary,
    onSurfaceVariant = MedKitInkSoft,

    outline          = MedKitLine,
    outlineVariant   = MedKitLine,

    error            = MedKitEmergencyRed,
    onError          = Color.White,
)

// ── MedKit typography scale ───────────────────────────────────────────────────
private val MedKitTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 26.sp,
        lineHeight = 32.sp,
        color      = MedKitInkMain
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 22.sp,
        lineHeight = 28.sp,
        color      = MedKitInkMain
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 15.sp,
        lineHeight = 20.sp,
        color      = MedKitInkMain
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 15.sp,
        lineHeight = 22.sp,
        color      = MedKitInkSoft
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 13.sp,
        lineHeight = 20.sp,
        color      = MedKitInkSoft
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 11.sp,
        lineHeight = 16.sp,
        color      = MedKitInkFaint
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 11.sp,
        color      = MedKitInkFaint
    )
)

@Composable
fun LoginAppTheme(
    content: @Composable () -> Unit
) {
    // MedKit currently ships a single light theme.
    // Dark mode support can be added later by checking isSystemInDarkTheme()
    // and providing a MedKitDarkColorScheme.
    val colorScheme = MedKitColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MedKitPrimaryDark.toArgb()
            // Status bar icons are light (white) on the dark green bar.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = MedKitTypography,
        content     = content
    )
}
