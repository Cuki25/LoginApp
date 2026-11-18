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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    email:           String = "",
    onCheckSymptoms: () -> Unit,
    onFirstAid:      () -> Unit,
    onHospitals:     () -> Unit,
    onDial:          () -> Unit
) {
    val firstName = email.substringBefore('@').replaceFirstChar { it.uppercase() }
        .ifBlank { "there" }

    val dateLabel = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        .format(Date()).uppercase()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top bar ───────────────────────────────────────────────────────────
        Row(
            modifier          = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "MedKit",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onPrimary,
                modifier   = Modifier.weight(1f)
            )
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {

            // ── Gradient greeting card ────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CardShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF2E7D5B), Color(0xFF1E5C43))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        dateLabel,
                        fontSize     = 10.sp,
                        fontWeight   = FontWeight.SemiBold,
                        color        = Color.White.copy(alpha = 0.75f),
                        letterSpacing = 1.2.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Hello, $firstName",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "STI College Global City · stay ready, help is one tap away.",
                        fontSize   = 12.sp,
                        color      = Color.White.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    // ── Coral CTA ─────────────────────────────────────────────
                    Button(
                        onClick  = onCheckSymptoms,
                        shape    = FieldShape,
                        colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFE56B61)),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Filled.MonitorHeart, null,
                            tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Check Symptoms", color = Color.White,
                            fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Quick access ──────────────────────────────────────────────────
            Text(
                "QUICK ACCESS",
                fontSize     = 11.sp,
                fontWeight   = FontWeight.SemiBold,
                color        = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(10.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickTile("First-Aid\nGuides",  Icons.Filled.MedicalServices, onFirstAid,  Modifier.weight(1f))
                QuickTile("Hospitals",           Icons.Filled.LocalHospital,  onHospitals, Modifier.weight(1f))
                QuickTile("Emergency\nLook-up",  Icons.Filled.Search,         {},          Modifier.weight(1f))
                QuickTile("Speed-\nDial",        Icons.Filled.Phone,          onDial,      Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))

            // ── Nearest health facilities ─────────────────────────────────────
            Text(
                "Nearest health facilities",
                fontSize   = 15.sp,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(10.dp))

            NearbyFacilityCard(
                name    = "St. Luke's Medical Center — Global City",
                detail  = "Rizal Drive corner 32nd St & 5th Ave, BGC, Taguig",
                hours   = "Open 24/7"
            )
            Spacer(Modifier.height(8.dp))
            NearbyFacilityCard(
                name    = "Medical Center of Taguig City, Inc.",
                detail  = "Levi Mariano Avenue, Brgy. Ususan, Taguig City",
                hours   = "Open 24 hours, 7 days a week"
            )

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick  = onHospitals,
                modifier = Modifier.fillMaxWidth(),
                shape    = FieldShape,
                border   = androidx.compose.foundation.BorderStroke(
                    1.dp, MaterialTheme.colorScheme.outline
                )
            ) {
                Text("View all facilities",
                    color    = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp)
            }

            Spacer(Modifier.height(16.dp))

            DisclaimerRow("MedKit guidance is preliminary first aid only. It does not replace professional medical care. Call 911 for a life-threatening emergency.")

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuickTile(
    label:   String,
    icon:    ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier.clickable(onClick = onClick),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape     = CardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier            = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 14.sp,
                textAlign  = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
private fun NearbyFacilityCard(name: String, detail: String, hours: String) {
    Card(
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape    = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier          = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Filled.LocalHospital, null,
                tint     = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp).padding(top = 2.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text(name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface)
                Text(detail, fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
                Spacer(Modifier.height(4.dp))
                Surface(
                    color = Color(0xFF1E7D4F),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        hours,
                        modifier     = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize     = 10.sp,
                        fontWeight   = FontWeight.SemiBold,
                        color        = Color.White
                    )
                }
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
        Icon(Icons.Filled.Warning, null,
            tint = Color(0xFF856404), modifier = Modifier.size(16.dp).padding(top = 2.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 11.sp, color = Color(0xFF856404), lineHeight = 16.sp)
    }
}
