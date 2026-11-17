package com.example.loginapp.ui.firstaid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.home.DisclaimerRow
import com.example.loginapp.ui.theme.*

@Composable
fun FirstAidListScreen(onModuleClick: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    val results = remember(query) { searchModules(query) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MedKitBackground)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .background(MedKitPrimary)
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .fillMaxWidth()
        ) {
            Text("First aid guide", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White)
        }

        // ── Search ────────────────────────────────────────────────────────────
        OutlinedTextField(
            value         = query,
            onValueChange = { query = it },
            placeholder   = { Text("Search guides…", color = MedKitInkFaint) },
            leadingIcon   = { Icon(Icons.Filled.Search, null, tint = MedKitInkFaint) },
            singleLine    = true,
            shape         = FieldShape,
            colors        = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MedKitSurface,
                focusedContainerColor   = MedKitSurface,
                unfocusedBorderColor    = MedKitLine,
                focusedBorderColor      = MedKitPrimary
            ),
            modifier      = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        if (results.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
                Text("No guides match your search.", color = MedKitInkFaint, fontSize = 13.sp)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(results) { module ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onModuleClick(module.id) },
                    colors = CardDefaults.cardColors(containerColor = MedKitSurface),
                    shape  = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier          = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(module.icon, fontSize = 24.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            module.title, modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MedKitInkMain
                        )
                        Icon(Icons.Filled.ChevronRight, null, tint = MedKitInkFaint)
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                DisclaimerRow("All ten modules are preliminary first-aid guidance only and never replace professional medical care.")
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
