package com.example.loginapp.ui.theme

import androidx.compose.ui.graphics.Color

// ── MedKit Brand Colors ───────────────────────────────────────────────────────

/** Primary action color — dark emerald green. */
val MedKitPrimary     = Color(0xFF0D5C4A)

/** Darker shade for pressed / status bar. */
val MedKitPrimaryDark = Color(0xFF093F33)

/** Very light tint for chips, selected states, pill badges. */
val MedKitPrimaryTint = Color(0xFFE1EFEA)

// ── Backgrounds & Surfaces ────────────────────────────────────────────────────

val MedKitBackground       = Color(0xFFEEF3F1)   // app scaffold background
val MedKitSurface          = Color(0xFFFFFFFF)   // card / dialog surface
val MedKitSurfaceSecondary = Color(0xFFF4F8F6)   // text field fill

// ── Ink (text) ────────────────────────────────────────────────────────────────

val MedKitInkMain  = Color(0xFF16231F)   // primary body text
val MedKitInkSoft  = Color(0xFF526158)   // secondary / label text
val MedKitInkFaint = Color(0xFF8B978F)   // placeholder / hint text

// ── Status & Semantic ─────────────────────────────────────────────────────────

val MedKitEmergencyRed = Color(0xFFC1372E)   // error, urgent alerts
val MedKitLine         = Color(0xFFDCE5E0)   // borders and dividers

// ── Corner Radii (shared constants via Dp) ────────────────────────────────────
// Usage:
//   RoundedCornerShape(FieldRadius)   → text fields, buttons
//   RoundedCornerShape(CardRadius)    → cards
//   RoundedCornerShape(CrestRadius)   → brand crest
//
// (These are kept here alongside colors for a single design-token import.)
