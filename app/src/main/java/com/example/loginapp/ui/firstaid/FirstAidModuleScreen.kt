package com.example.loginapp.ui.firstaid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.home.DisclaimerRow
import com.example.loginapp.ui.theme.*

@Composable
fun FirstAidModuleScreen(
    moduleId:  String,
    onBack:    () -> Unit,
    onDial:    () -> Unit,
    onHospitals: () -> Unit
) {
    val module = firstAidModules.firstOrNull { it.id == moduleId }
        ?: firstAidModules.first()

    var currentStep by remember { mutableIntStateOf(0) }
    val steps = module.doSteps

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MedKitBackground)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────────
        Row(
            modifier          = Modifier
                .background(MedKitPrimary)
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
            Text("First-aid module", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                color = Color.White, modifier = Modifier.weight(1f))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // ── Module header ─────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(module.icon, fontSize = 32.sp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(module.title, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        color = MedKitInkMain)
                    Text("${steps.size} steps", fontSize = 12.sp, color = MedKitInkFaint)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Step navigator ────────────────────────────────────────────────
            Text(
                "STEP ${currentStep + 1} OF ${steps.size}",
                fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                color = MedKitPrimary, letterSpacing = 1.sp
            )
            Spacer(Modifier.height(8.dp))

            Card(
                colors   = CardDefaults.cardColors(containerColor = MedKitSurface),
                shape    = CardShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    steps[currentStep],
                    modifier   = Modifier.padding(18.dp),
                    fontSize   = 15.sp,
                    lineHeight = 24.sp,
                    color      = MedKitInkMain
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick  = { if (currentStep > 0) currentStep-- },
                    enabled  = currentStep > 0,
                    shape    = FieldShape,
                    modifier = Modifier.weight(1f)
                ) { Text("Prev") }
                Button(
                    onClick  = { if (currentStep < steps.size - 1) currentStep++ },
                    enabled  = currentStep < steps.size - 1,
                    shape    = FieldShape,
                    colors   = ButtonDefaults.buttonColors(containerColor = MedKitPrimary),
                    modifier = Modifier.weight(1f)
                ) { Text("Next") }
            }

            // ── Step dots ─────────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                steps.forEachIndexed { i, _ ->
                    Box(
                        modifier = Modifier
                            .padding(3.dp)
                            .size(if (i == currentStep) 10.dp else 7.dp)
                            .clip(CircleShape)
                            .background(if (i == currentStep) MedKitPrimary else MedKitLine)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── What to do (all steps) ────────────────────────────────────────
            SectionHeader("What to do")
            steps.forEachIndexed { i, step ->
                NumberedRow(i + 1, step)
                Spacer(Modifier.height(6.dp))
            }

            Spacer(Modifier.height(16.dp))

            SectionHeader("Avoid these")
            module.avoidSteps.forEach { step ->
                BulletRow(step)
                Spacer(Modifier.height(6.dp))
            }

            Spacer(Modifier.height(24.dp))

            // ── Action buttons ────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick  = onDial,
                    shape    = FieldShape,
                    modifier = Modifier.weight(1f)
                ) { Text("Speed-dial", color = MedKitPrimary, fontSize = 13.sp) }
                OutlinedButton(
                    onClick  = onHospitals,
                    shape    = FieldShape,
                    modifier = Modifier.weight(1f)
                ) { Text("Find hospital", color = MedKitPrimary, fontSize = 13.sp) }
            }

            Spacer(Modifier.height(16.dp))

            DisclaimerRow("This guidance is preliminary first aid and does not replace professional medical care. Call 911 for a life-threatening emergency.")

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MedKitInkMain)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun NumberedRow(n: Int, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(MedKitPrimary)
        ) {
            Text("$n", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(10.dp))
        Text(text, fontSize = 13.sp, color = MedKitInkSoft, lineHeight = 20.sp,
            modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun BulletRow(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(MedKitEmergencyRed)
        )
        Spacer(Modifier.width(10.dp))
        Text(text, fontSize = 13.sp, color = MedKitInkSoft, lineHeight = 20.sp)
    }
}
