package com.example.loginapp.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
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

// ── Light color scheme ────────────────────────────────────────────────────────
private val MedKitLightColorScheme = lightColorScheme(
    primary            = MedKitPrimary,
    onPrimary          = Color.White,
    primaryContainer   = MedKitPrimaryTint,
    onPrimaryContainer = MedKitPrimaryDark,

    secondary          = MedKitPrimaryDark,
    onSecondary        = Color.White,

    background         = MedKitBackground,
    onBackground       = MedKitInkMain,

    surface            = MedKitSurface,
    onSurface          = MedKitInkMain,
    surfaceVariant     = MedKitSurfaceSecondary,
    onSurfaceVariant   = MedKitInkSoft,

    outline            = MedKitLine,
    outlineVariant     = MedKitLine,

    error              = MedKitEmergencyRed,
    onError            = Color.White,
)

// ── Dark color scheme (Forest Green) ─────────────────────────────────────────
private val MedKitDarkColorScheme = darkColorScheme(
    primary            = MedKitDarkPrimary,
    onPrimary          = MedKitDarkOnPrimary,
    primaryContainer   = MedKitDarkSurface,
    onPrimaryContainer = MedKitDarkPrimary,

    secondary          = MedKitDarkPrimary,
    onSecondary        = MedKitDarkOnPrimary,

    background         = MedKitDarkBackground,
    onBackground       = MedKitDarkInkMain,

    surface            = MedKitDarkSurface,
    onSurface          = MedKitDarkInkMain,
    surfaceVariant     = MedKitDarkSurfaceSecondary,
    onSurfaceVariant   = MedKitDarkInkSoft,

    outline            = MedKitDarkLine,
    outlineVariant     = MedKitDarkLine,

    error              = MedKitDarkEmergencyRed,
    onError            = MedKitDarkBackground,
)

// ── Typography ────────────────────────────────────────────────────────────────
private val MedKitTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold,    fontSize = 26.sp, lineHeight = 32.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium   = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 20.sp),
    bodyLarge     = TextStyle(fontWeight = FontWeight.Normal,  fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium    = TextStyle(fontWeight = FontWeight.Normal,  fontSize = 13.sp, lineHeight = 20.sp),
    bodySmall     = TextStyle(fontWeight = FontWeight.Normal,  fontSize = 11.sp, lineHeight = 16.sp),
    labelSmall    = TextStyle(fontWeight = FontWeight.Medium,  fontSize = 11.sp),
)

@Composable
fun LoginAppTheme(
    darkTheme: Boolean = false,
    content:   @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) MedKitDarkColorScheme else MedKitLightColorScheme
    val statusBarColor = if (darkTheme) MedKitDarkBackground.toArgb()
                         else MedKitPrimaryDark.toArgb()
    val lightStatusIcons = darkTheme  // dark mode → light background → light icons

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = statusBarColor
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = lightStatusIcons
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = MedKitTypography,
        content     = content
    )
}
