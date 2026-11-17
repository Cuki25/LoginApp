package com.example.loginapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.theme.*

@Composable
fun HomeScreen(
    onCheckSymptoms: () -> Unit,
    onFirstAid:      () -> Unit,
    onHospitals:     () -> Unit,
    onDial:          () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MedKitBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // ── Date chip ────────────────────────────────────────────────────────
        Surface(
            color = MedKitPrimaryTint,
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                "TODAY",
                modifier     = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                fontSize     = 11.sp,
                fontWeight   = FontWeight.SemiBold,
                color        = MedKitPrimary,
                letterSpacing = 1.sp
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            "Hello there 👋",
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold,
            color      = MedKitInkMain
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Stay ready. Your campus emergency guide is one tap away.",
            fontSize = 14.sp,
            color    = MedKitInkSoft,
            lineHeight = 20.sp
        )

        Spacer(Modifier.height(24.dp))

        // ── Check Symptoms CTA ────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onCheckSymptoms),
            colors = CardDefaults.cardColors(containerColor = MedKitPrimary),
            shape  = CardShape
        ) {
            Row(
                modifier            = Modifier.padding(18.dp),
                verticalAlignment   = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "Check Symptoms",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp,
                        color      = Color.White
                    )
                    Text(
                        "Tap to find the right first-aid module",
                        fontSize = 12.sp,
                        color    = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "Quick access",
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color      = MedKitInkFaint,
            letterSpacing = 0.5.sp
        )

        Spacer(Modifier.height(10.dp))

        // ── Quick-access grid ─────────────────────────────────────────────────
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickTile(
                icon    = Icons.Filled.MedicalServices,
                label   = "First Aid",
                onClick = onFirstAid,
                modifier = Modifier.weight(1f)
            )
            QuickTile(
                icon    = Icons.Filled.LocalHospital,
                label   = "Hospitals",
                onClick = onHospitals,
                modifier = Modifier.weight(1f)
            )
            QuickTile(
                icon    = Icons.Filled.Phone,
                label   = "Speed Dial",
                onClick = onDial,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Nearest health facilities",
            fontSize   = 15.sp,
            fontWeight = FontWeight.Bold,
            color      = MedKitInkMain
        )

        Spacer(Modifier.height(10.dp))

        NearbyFacilityRow("STI Clinic (Campus)", "Ground Floor, Main Bldg")
        Spacer(Modifier.height(8.dp))
        NearbyFacilityRow("Taguig City Hospital", "0.8 km away")
        Spacer(Modifier.height(8.dp))
        NearbyFacilityRow("BGC Medical Center", "1.2 km away")

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onHospitals,
            modifier = Modifier.fillMaxWidth(),
            shape    = FieldShape,
            border   = androidx.compose.foundation.BorderStroke(1.dp, MedKitLine)
        ) {
            Text("View all facilities", color = MedKitPrimary, fontSize = 13.sp)
        }

        Spacer(Modifier.height(24.dp))

        // ── Readiness tip ─────────────────────────────────────────────────────
        Card(
            colors = CardDefaults.cardColors(containerColor = MedKitPrimaryTint),
            shape  = CardShape,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Readiness tip",
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = MedKitPrimary,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Know your nearest exit",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp,
                    color      = MedKitInkMain
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Identify the fastest route from your classroom to the school clinic and the main gate before an emergency happens — not during one.",
                    fontSize   = 13.sp,
                    color      = MedKitInkSoft,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Disclaimer ────────────────────────────────────────────────────────
        DisclaimerRow("MedKit guidance is preliminary first aid only. It does not replace professional medical care. Call 911 for a life-threatening emergency.")
    }
}

@Composable
private fun QuickTile(
    icon:     ImageVector,
    label:    String,
    onClick:  () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors   = CardDefaults.cardColors(containerColor = MedKitSurface),
        shape    = CardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier            = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MedKitPrimaryTint)
            ) {
                Icon(icon, contentDescription = null, tint = MedKitPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MedKitInkMain)
        }
    }
}

@Composable
private fun NearbyFacilityRow(name: String, detail: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MedKitSurface),
        shape  = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier          = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.LocalHospital, contentDescription = null,
                tint = MedKitPrimary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text(name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MedKitInkMain)
                Text(detail, fontSize = 12.sp, color = MedKitInkFaint)
            }
        }
    }
}

@Composable
fun DisclaimerRow(text: String) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFFF3CD))
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(Icons.Filled.Warning, contentDescription = null,
            tint = Color(0xFF856404), modifier = Modifier.size(16.dp).padding(top = 2.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 11.sp, color = Color(0xFF856404), lineHeight = 16.sp)
    }
}
