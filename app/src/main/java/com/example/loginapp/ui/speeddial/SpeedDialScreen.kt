package com.example.loginapp.ui.speeddial

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.theme.*

data class DialEntry(val label: String, val number: String)

private val emergencyServices = listOf(
    DialEntry("911 — National Emergency", "911"),
    DialEntry("Red Cross PH", "143"),
    DialEntry("NDRRMC Emergency", "911")
)

private val campusLines = listOf(
    DialEntry("STI Campus Clinic", "(02)8632-5423"),
    DialEntry("STI Security Office", "(02)8632-5000"),
    DialEntry("STI Main Reception", "(02)8632-5100")
)

@Composable
fun SpeedDialScreen() {
    val context = LocalContext.current

    fun dial(number: String) {
        val clean = number.replace(Regex("[^0-9+]"), "")
        context.startActivity(
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:$clean"))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .fillMaxWidth()
        ) {
            Text("Speed-dial", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("One-touch dial buttons. Each opens your phone dialer with the number filled in — you press Call yourself. MedKit never places a call or sends an SMS on its own.",
                fontSize = 13.sp, color = MedKitInkSoft, lineHeight = 20.sp)

            Spacer(Modifier.height(20.dp))
            SectionLabel("Local emergency services")
            emergencyServices.forEach { entry ->
                DialButton(entry) { dial(entry.number) }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(16.dp))
            SectionLabel("Campus lines")
            campusLines.forEach { entry ->
                DialButton(entry) { dial(entry.number) }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(16.dp))
            SectionLabel("My emergency contacts")

            Card(
                colors = CardDefaults.cardColors(containerColor = MedKitSurface),
                shape  = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(Modifier.padding(16.dp)) {
                    Text(
                        "No personal contacts added yet.\nAdd them in your Profile.",
                        fontSize = 13.sp, color = MedKitInkFaint, lineHeight = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                colors   = CardDefaults.cardColors(containerColor = MedKitPrimaryTint),
                shape    = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.Phone, null, tint = MedKitPrimary,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "If a look-up result is on screen, its emergency contacts also appear here as one-touch buttons.",
                        fontSize = 12.sp, color = MedKitPrimary, lineHeight = 18.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
        color = MedKitInkFaint, letterSpacing = 0.5.sp)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun DialButton(entry: DialEntry, onClick: () -> Unit) {
    Button(
        onClick  = onClick,
        shape    = FieldShape,
        colors   = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor   = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier.fillMaxWidth().height(52.dp)
    ) {
        Icon(Icons.Filled.Phone, null,
            tint     = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text("${entry.label}  ${entry.number}",
            fontWeight = FontWeight.Medium,
            fontSize   = 14.sp,
            color      = MaterialTheme.colorScheme.onPrimary)
    }
}
