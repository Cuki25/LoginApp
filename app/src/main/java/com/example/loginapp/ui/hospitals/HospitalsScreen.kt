package com.example.loginapp.ui.hospitals

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.home.DisclaimerRow
import com.example.loginapp.ui.theme.*

data class Hospital(
    val name:     String,
    val city:     String,
    val address:  String,
    val contact:  String,
    val hours:    String,
    val services: List<String>
)

private val hospitals = listOf(
    Hospital(
        name     = "St. Luke's Medical Center — Global City",
        city     = "Taguig City",
        address  = "Rizal Drive corner 32nd Street and 5th Avenue, Bonifacio Global City, Taguig City",
        contact  = "(02) 8789 7700",
        hours    = "Open 24/7",
        services = listOf("Emergency room", "Trauma care", "Intensive care", "Laboratory")
    ),
    Hospital(
        name     = "Medical Center of Taguig City, Inc.",
        city     = "Taguig City",
        address  = "Levi Mariano Avenue, Barangay Ususan, Taguig City",
        contact  = "(02) 8888 6284",
        hours    = "Open 24/7",
        services = listOf("Emergency room", "Outpatient care", "Laboratory", "Pharmacy")
    ),
    Hospital(
        name     = "Makati Life Medical Center",
        city     = "Makati City",
        address  = "Metropolitan Avenue and Malugay, Makati City, 1209 Metro Manila",
        contact  = "(02) 8888 2020",
        hours    = "Open 24/7",
        services = listOf("Emergency room", "Outpatient care", "Imaging", "Pharmacy")
    ),
    Hospital(
        name     = "Ospital ng Makati",
        city     = "Makati City",
        address  = "Sampaguita Street corner Apitong Street, Barangay Pembo, Makati City",
        contact  = "(02) 8882 6316",
        hours    = "Open 24/7",
        services = listOf("Emergency room", "Outpatient care", "Maternity", "Pharmacy")
    ),
    Hospital(
        name     = "Allied Care Experts (ACE) Medical Center — Pateros",
        city     = "Pateros",
        address  = "884 P. Herrera Street, Brgy. Aguho, Pateros, Metro Manila",
        contact  = "(02) 8706 9395",
        hours    = "Open 24/7",
        services = listOf("Emergency room", "Outpatient care", "Laboratory")
    ),
    Hospital(
        name     = "Rizal Medical Center",
        city     = "Pasig City",
        address  = "425 Pasig Blvd, Pasig, Philippines, 1600",
        contact  = "(02) 8865 8400",
        hours    = "Open 24/7",
        services = listOf("Emergency room", "Trauma care", "Outpatient care", "Laboratory")
    ),
)

private val cities = listOf("All", "Taguig City", "Makati City", "Pateros", "Pasig City")

@Composable
fun HospitalsScreen() {
    val context        = LocalContext.current
    var query          by remember { mutableStateOf("") }
    var selectedCity   by remember { mutableStateOf("All") }

    val results = remember(query, selectedCity) {
        hospitals.filter { h ->
            (selectedCity == "All" || h.city == selectedCity) &&
            (query.isBlank() ||
                h.name.contains(query, ignoreCase = true) ||
                h.address.contains(query, ignoreCase = true))
        }
    }

    fun openMaps(address: String) {
        val uri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    fun dial(number: String) {
        val clean = number.replace(Regex("[^0-9+]"), "")
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$clean")))
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
            Text("Health facilities", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary)
        }

        // ── Search bar ────────────────────────────────────────────────────────
        OutlinedTextField(
            value         = query,
            onValueChange = { query = it },
            placeholder   = { Text("Search facilities…", color = MaterialTheme.colorScheme.onSurfaceVariant) },
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

        // ── City filter chips ─────────────────────────────────────────────────
        Row(
            modifier            = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            cities.forEach { city ->
                FilterChip(
                    selected = selectedCity == city,
                    onClick  = { selectedCity = city },
                    label    = { Text(city, fontSize = 12.sp) },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor    = MaterialTheme.colorScheme.primary,
                        selectedLabelColor        = MaterialTheme.colorScheme.onPrimary,
                        selectedLeadingIconColor  = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        if (results.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
                Text("No hospitals match your search.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(results) { h ->
                HospitalCard(
                    hospital    = h,
                    onRoute     = { openMaps(h.address) },
                    onCall      = { dial(h.contact) }
                )
            }
            item {
                Spacer(Modifier.height(8.dp))
                DisclaimerRow("Six curated facilities listed for reference. Always call 911 for a life-threatening emergency, then head to the nearest ER.")
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HospitalCard(hospital: Hospital, onRoute: () -> Unit, onCall: () -> Unit) {
    Card(
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape    = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            // Name + 24/7 badge
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Filled.LocalHospital, null,
                    tint     = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp).padding(top = 2.dp))
                Spacer(Modifier.width(6.dp))
                Text(hospital.name, fontWeight = FontWeight.Bold,
                    fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f))
                Spacer(Modifier.width(6.dp))
                Surface(
                    color = Color(0xFF1E7D4F),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(hospital.hours,
                        modifier   = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize   = 10.sp, fontWeight = FontWeight.SemiBold,
                        color      = Color.White)
                }
            }

            Spacer(Modifier.height(6.dp))

            // Address
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Filled.LocationOn, null,
                    tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp).padding(top = 2.dp))
                Spacer(Modifier.width(4.dp))
                Text(hospital.address, fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
            }

            Spacer(Modifier.height(4.dp))

            // Contact
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Phone, null,
                    tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(4.dp))
                Text(hospital.contact, fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(8.dp))

            // Service chips
            Row(
                modifier              = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                hospital.services.forEach { svc ->
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(svc,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 10.sp,
                            color    = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick  = onRoute,
                    shape    = FieldShape,
                    colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text("Details & route", fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary)
                }
                OutlinedButton(
                    onClick  = onCall,
                    shape    = FieldShape,
                    border   = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Icon(Icons.Filled.Phone, null,
                        tint     = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Call", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
