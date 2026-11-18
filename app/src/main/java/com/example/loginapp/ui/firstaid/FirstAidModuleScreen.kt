package com.example.loginapp.ui.firstaid

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.home.DisclaimerRow
import com.example.loginapp.ui.theme.*
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun FirstAidModuleScreen(
    moduleId:    String,
    onBack:      () -> Unit,
    onDial:      () -> Unit,
    onHospitals: () -> Unit
) {
    val module  = firstAidModules.firstOrNull { it.id == moduleId } ?: firstAidModules.first()
    val steps   = module.doSteps
    val context = LocalContext.current

    var currentStep   by remember { mutableIntStateOf(0) }
    var autoPlay      by remember { mutableStateOf(false) }
    var ttsReady      by remember { mutableStateOf(false) }
    var isPaused      by remember { mutableStateOf(false) }

    // ── TTS setup ─────────────────────────────────────────────────────────────
    val tts = remember {
        TextToSpeech(context, null)
    }
    DisposableEffect(Unit) {
        onDispose { tts.stop(); tts.shutdown() }
    }

    // Auto-advance coroutine: waits for TTS done, delays 1.5 s, advances step
    LaunchedEffect(autoPlay, currentStep) {
        if (!autoPlay || isPaused) return@LaunchedEffect
        val stepText = "Step ${currentStep + 1}. ${steps[currentStep]}"
        val params   = android.os.Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "step_$currentStep")

        var done = false
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?)  { done = true }
            override fun onError(utteranceId: String?) { done = true }
        })
        tts.speak(stepText, TextToSpeech.QUEUE_FLUSH, params, "step_$currentStep")

        // Poll until TTS signals done
        while (!done) delay(200)
        delay(1500)

        if (autoPlay && !isPaused) {
            if (currentStep < steps.size - 1) currentStep++
            else autoPlay = false   // end of module — stop auto-play
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────────
        Row(
            modifier          = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { tts.stop(); onBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null,
                    tint = MaterialTheme.colorScheme.onPrimary)
            }
            Text("First-aid module", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.weight(1f))
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
                        color = MaterialTheme.colorScheme.onBackground)
                    Text("${steps.size} steps", fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Step infographic placeholder ──────────────────────────────────
            Card(
                colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape    = CardShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CardShape)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Image, null,
                            tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(6.dp))
                        Text("Step ${currentStep + 1} Visual Guide",
                            fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Infographic placeholder",
                            fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Step navigator ────────────────────────────────────────────────
            Text(
                "STEP ${currentStep + 1} OF ${steps.size}",
                fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary, letterSpacing = 1.sp
            )
            Spacer(Modifier.height(8.dp))

            Card(
                colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape    = CardShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(steps[currentStep], modifier = Modifier.padding(18.dp),
                    fontSize = 15.sp, lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(Modifier.height(12.dp))

            // ── Prev / Next controls ──────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick  = { tts.stop(); if (currentStep > 0) currentStep-- },
                    enabled  = currentStep > 0,
                    shape    = FieldShape,
                    modifier = Modifier.weight(1f)
                ) { Text("Prev") }
                Button(
                    onClick  = { tts.stop(); if (currentStep < steps.size - 1) currentStep++ },
                    enabled  = currentStep < steps.size - 1,
                    shape    = FieldShape,
                    colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1f)
                ) { Text("Next", color = MaterialTheme.colorScheme.onPrimary) }
            }

            Spacer(Modifier.height(8.dp))

            // ── Hands-Free Auto-Play ──────────────────────────────────────────
            Card(
                colors   = CardDefaults.cardColors(
                    containerColor = if (autoPlay)
                        MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface
                ),
                shape    = FieldShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier          = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (autoPlay && !isPaused) Icons.Filled.RecordVoiceOver
                        else Icons.Filled.PlayArrow,
                        null,
                        tint     = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Hands-Free Auto-Play", fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface)
                        Text("TTS reads each step and auto-advances",
                            fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked         = autoPlay,
                        onCheckedChange = {
                            autoPlay  = it
                            isPaused  = false
                            if (!it) tts.stop()
                        },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                if (autoPlay) {
                    Row(
                        modifier              = Modifier.padding(horizontal = 16.dp).padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick  = { isPaused = !isPaused; if (isPaused) tts.stop() },
                            shape    = FieldShape,
                            modifier = Modifier.weight(1f)
                        ) { Text(if (isPaused) "Resume" else "Pause") }
                        OutlinedButton(
                            onClick  = { tts.stop(); currentStep = 0; isPaused = false },
                            shape    = FieldShape,
                            modifier = Modifier.weight(1f)
                        ) { Text("Restart") }
                    }
                }
            }

            // ── Step dots ─────────────────────────────────────────────────────
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                steps.forEachIndexed { i, _ ->
                    Box(
                        modifier = Modifier
                            .padding(3.dp)
                            .size(if (i == currentStep) 10.dp else 7.dp)
                            .clip(CircleShape)
                            .background(
                                if (i == currentStep) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline
                            )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

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

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = onDial,  shape = FieldShape, modifier = Modifier.weight(1f)) {
                    Text("Speed-dial", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                }
                OutlinedButton(onClick = onHospitals, shape = FieldShape, modifier = Modifier.weight(1f)) {
                    Text("Find hospital", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            DisclaimerRow("This guidance is preliminary first aid and does not replace professional medical care. Call 911 for a life-threatening emergency.")

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable private fun SectionHeader(text: String) {
    Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground)
    Spacer(Modifier.height(8.dp))
}

@Composable private fun NumberedRow(n: Int, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier.size(22.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Text("$n", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(10.dp))
        Text(text, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable private fun BulletRow(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.padding(top = 6.dp).size(6.dp).clip(CircleShape)
            .background(MaterialTheme.colorScheme.error))
        Spacer(Modifier.width(10.dp))
        Text(text, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
    }
}
