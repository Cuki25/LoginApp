package com.example.loginapp.ui.symptoms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.firstaid.firstAidModules
import com.example.loginapp.ui.home.DisclaimerRow
import com.example.loginapp.ui.theme.*

/** Two-question symptom checker that recommends a first-aid module. */
@Composable
fun CheckSymptomsScreen(
    onBack:         () -> Unit,
    onModuleResult: (moduleId: String) -> Unit
) {
    // Simple two-step flow: pick a category → pick a sub-symptom
    data class SymptomCategory(val label: String, val moduleId: String)

    val categories = listOf(
        SymptomCategory("Severe bleeding / wound", "bleeding"),
        SymptomCategory("Choking / airway blocked", "choking"),
        SymptomCategory("Burns / scalds", "burns"),
        SymptomCategory("Broken bone / fracture", "fracture"),
        SymptomCategory("Fainted / unconscious", "fainting"),
        SymptomCategory("Seizure / convulsion", "seizure"),
        SymptomCategory("Severe allergic reaction", "allergic"),
        SymptomCategory("Heat stroke / overheating", "heatstroke"),
        SymptomCategory("Nosebleed", "nosebleed"),
        SymptomCategory("Low blood sugar / diabetic", "diabetic")
    )

    var step     by remember { mutableIntStateOf(0) }   // 0=picker, 1=confirm, 2=result
    var selected by remember { mutableStateOf<SymptomCategory?>(null) }

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
            IconButton(onClick = { if (step > 0) step-- else onBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
            Text("Check symptoms", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                color = Color.White, modifier = Modifier.weight(1f))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            when (step) {
                0 -> {
                    // ── Step 1: pick symptom ──────────────────────────────────
                    Text(
                        "Tap the symptom that best matches what you see.",
                        style = MaterialTheme.typography.bodyMedium, color = MedKitInkSoft
                    )
                    Spacer(Modifier.height(16.dp))
                    categories.forEach { cat ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selected = cat; step = 1 },
                            colors = CardDefaults.cardColors(containerColor = MedKitSurface),
                            shape  = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                cat.label,
                                modifier   = Modifier.padding(16.dp),
                                fontSize   = 14.sp,
                                color      = MedKitInkMain,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                1 -> {
                    // ── Step 2: confirm ───────────────────────────────────────
                    val module = firstAidModules.firstOrNull { it.id == selected?.moduleId }

                    Text("QUESTION 1 OF 2", fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                        color = MedKitPrimary, letterSpacing = 1.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Is the person showing: ${selected?.label}?",
                        fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MedKitInkMain
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Confirm before we show you the first-aid module.",
                        style = MaterialTheme.typography.bodyMedium, color = MedKitInkSoft
                    )
                    Spacer(Modifier.height(24.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick  = { selected?.moduleId?.let { onModuleResult(it) } },
                            shape    = FieldShape,
                            colors   = ButtonDefaults.buttonColors(containerColor = MedKitPrimary),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) { Text("Yes") }
                        OutlinedButton(
                            onClick  = { step = 0 },
                            shape    = FieldShape,
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) { Text("No") }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            DisclaimerRow("MedKit covers ten first-aid scenarios only and never diagnoses a condition.")
        }
    }
}
