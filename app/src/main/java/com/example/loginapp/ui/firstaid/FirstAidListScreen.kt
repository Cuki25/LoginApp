package com.example.loginapp.ui.firstaid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.theme.*

private val severityFilters = listOf("All", "Critical", "Urgent", "Moderate")

private fun severityColor(severity: String): Color = when (severity) {
    "Critical" -> Color(0xFFE53935)
    "Urgent"   -> Color(0xFFFB8C00)
    else       -> Color(0xFF43A047)
}

@Composable
fun FirstAidListScreen(onModuleClick: (String) -> Unit) {
    var query          by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val results = remember(query, selectedFilter) {
        firstAidModules.filter { m ->
            (selectedFilter == "All" || m.severity == selectedFilter) &&
            (query.isBlank() ||
                m.title.lowercase().contains(query.lowercase()) ||
                m.symptoms.any { it.contains(query.lowercase()) })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .fillMaxWidth()
        ) {
            Text("First-aid guide", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary)
        }

        // ── Search ────────────────────────────────────────────────────────────
        OutlinedTextField(
            value         = query,
            onValueChange = { query = it },
            placeholder   = { Text("Search guides…", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon   = { Icon(Icons.Filled.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            singleLine    = true,
            shape         = FieldShape,
            colors        = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor   = MaterialTheme.colorScheme.surface,
                unfocusedBorderColor    = MaterialTheme.colorScheme.outline,
                focusedBorderColor      = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // ── Severity filter chips ─────────────────────────────────────────────
        Row(
            modifier              = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            severityFilters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick  = { selectedFilter = filter },
                    label    = { Text(filter, fontSize = 12.sp) },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor   = MaterialTheme.colorScheme.primary,
                        selectedLabelColor       = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        if (results.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
                Text("No guides match your search.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyVerticalGrid(
                columns            = GridCells.Fixed(2),
                contentPadding     = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement   = Arrangement.spacedBy(10.dp),
                modifier              = Modifier.fillMaxSize()
            ) {
                items(results) { module ->
                    FirstAidTile(module = module, onClick = { onModuleClick(module.id) })
                }
            }
        }
    }
}

@Composable
private fun FirstAidTile(module: FirstAidModule, onClick: () -> Unit) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape     = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier            = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(module.icon, fontSize = 28.sp)
            Spacer(Modifier.height(8.dp))
            Text(module.title, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface, lineHeight = 18.sp)
            Spacer(Modifier.height(6.dp))
            // Severity badge
            Surface(
                color = severityColor(module.severity).copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    module.severity,
                    modifier   = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = severityColor(module.severity)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "${module.doSteps.size} steps · read aloud",
                fontSize = 10.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )
        }
    }
}
