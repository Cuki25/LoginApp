package com.example.loginapp.ui.lookup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.theme.*

@Composable
fun LookupScreen() {
    var searchMode  by remember { mutableStateOf(0) }     // 0 = by ID, 1 = by name
    var query       by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf<FakeStudent?>(null) }
    var searched    by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MedKitBackground)
    ) {
        Column(
            modifier = Modifier
                .background(MedKitPrimary)
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .fillMaxWidth()
        ) {
            Text("Emergency look-up", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                color = Color.White)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ── Tab row ───────────────────────────────────────────────────────
            TabRow(
                selectedTabIndex = searchMode,
                containerColor   = MedKitSurface,
                contentColor     = MedKitPrimary,
                modifier         = Modifier.fillMaxWidth()
            ) {
                Tab(selected = searchMode == 0, onClick = { searchMode = 0; query = ""; searched = false }) {
                    Text("Search by Student ID", modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Tab(selected = searchMode == 1, onClick = { searchMode = 1; query = ""; searched = false }) {
                    Text("Search by name", modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Search field ──────────────────────────────────────────────────
            OutlinedTextField(
                value         = query,
                onValueChange = { query = it },
                placeholder   = {
                    Text(if (searchMode == 0) "Enter Student ID…" else "Enter full name…",
                        color = MedKitInkFaint)
                },
                leadingIcon   = {
                    Icon(if (searchMode == 0) Icons.Filled.Person else Icons.Filled.Search,
                        null, tint = MedKitInkFaint)
                },
                singleLine    = true,
                shape         = FieldShape,
                colors        = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MedKitSurface,
                    focusedContainerColor   = MedKitSurface,
                    unfocusedBorderColor    = MedKitLine,
                    focusedBorderColor      = MedKitPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    searched = true
                    searchResult = fakeSearch(query, byId = searchMode == 0)
                },
                shape    = FieldShape,
                colors   = ButtonDefaults.buttonColors(containerColor = MedKitPrimary),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Search student", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }

            if (searched) {
                Spacer(Modifier.height(16.dp))
                if (searchResult == null) {
                    Text("No student found.", color = MedKitInkFaint, fontSize = 13.sp)
                } else {
                    StudentResultCard(searchResult!!)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StudentResultCard(student: FakeStudent) {
    Card(
        colors   = CardDefaults.cardColors(containerColor = MedKitSurface),
        shape    = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            // Avatar initials
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier
                        .size(48.dp)
                        .background(MedKitPrimary, RoundedCornerShape(12.dp))
                ) {
                    Text(
                        student.initials,
                        fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        color    = Color.White
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(student.name, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                        color = MedKitInkMain)
                    Text(student.studentId, fontSize = 12.sp, color = MedKitInkFaint)
                    Text("STI College Global City", fontSize = 12.sp, color = MedKitInkFaint)
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MedKitLine)
            Spacer(Modifier.height(12.dp))

            InfoRow("Blood type",  student.bloodType)
            InfoRow("Allergies",   student.allergies)
            InfoRow("Conditions",  student.conditions)
            InfoRow("Emergency contact", "${student.contactName} — ${student.contactNumber}")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.padding(vertical = 4.dp)) {
        Text("$label: ", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MedKitInkMain)
        Text(value, fontSize = 13.sp, color = MedKitInkSoft)
    }
}

// ── Fake data for demo ────────────────────────────────────────────────────────

data class FakeStudent(
    val name:          String,
    val initials:      String,
    val studentId:     String,
    val bloodType:     String,
    val allergies:     String,
    val conditions:    String,
    val contactName:   String,
    val contactNumber: String
)

private val fakeStudents = listOf(
    FakeStudent("Juan Dela Cruz", "JD", "STI-2024-001", "O+", "None known", "None known",
        "Maria Dela Cruz", "09171234567"),
    FakeStudent("Ana Santos", "AS", "STI-2024-002", "A+", "Penicillin", "Asthma",
        "Pedro Santos", "09181234567")
)

private fun fakeSearch(query: String, byId: Boolean): FakeStudent? {
    if (query.isBlank()) return null
    return if (byId) {
        fakeStudents.firstOrNull { it.studentId.contains(query, ignoreCase = true) }
    } else {
        fakeStudents.firstOrNull { it.name.contains(query, ignoreCase = true) }
    }
}
