package com.example.loginapp.ui.hospitals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
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

data class HealthFacility(
    val name:    String,
    val address: String,
    val phone:   String,
    val type:    String     // "hospital" | "clinic" | "urgent care"
)

private val facilities = listOf(
    HealthFacility("STI Campus Clinic", "Ground Floor, Main Bldg, STI Global City", "(02) 8632-5423", "clinic"),
    HealthFacility("Taguig-Pateros District Hospital", "Signal Village, Taguig City", "(02) 8838-7050", "hospital"),
    HealthFacility("BGC Medical Center", "9th Ave, BGC, Taguig", "(02) 8822-0000", "hospital"),
    HealthFacility("Makati Medical Center", "2 Amorsolo St, Makati", "(02) 8888-8999", "hospital"),
    HealthFacility("St. Luke's Medical Center – BGC", "5th Ave, BGC, Taguig", "(02) 8789-7700", "hospital"),
    HealthFacility("Asian Hospital and Medical Center", "2205 Civic Dr, Alabang", "(02) 8771-9000", "hospital")
)

@Composable
fun HospitalsScreen() {
    var query by remember { mutableStateOf("") }
    val results = remember(query) {
        if (query.isBlank()) facilities
        else facilities.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.address.contains(query, ignoreCase = true)
        }
    }

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
            Text("Nearby hospitals", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White)
        }

        OutlinedTextField(
            value         = query,
            onValueChange = { query = it },
            placeholder   = { Text("Search facilities…", color = MedKitInkFaint) },
            leadingIcon   = { Icon(Icons.Filled.Search, null, tint = MedKitInkFaint) },
            singleLine    = true,
            shape         = FieldShape,
            colors        = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MedKitSurface,
                focusedContainerColor   = MedKitSurface,
                unfocusedBorderColor    = MedKitLine,
                focusedBorderColor      = MedKitPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        if (results.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
                Text("No hospitals match your search.", color = MedKitInkFaint, fontSize = 13.sp)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(results) { facility ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MedKitSurface),
                    shape  = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocationOn, null, tint = MedKitPrimary,
                                modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(facility.name, fontWeight = FontWeight.Bold,
                                fontSize = 14.sp, color = MedKitInkMain)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(facility.address, fontSize = 12.sp, color = MedKitInkSoft)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Phone, null, tint = MedKitInkFaint,
                                modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(facility.phone, fontSize = 12.sp, color = MedKitInkFaint)
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(8.dp))
                DisclaimerRow("Six curated facilities, listed for reference and subject to change. Always call 911 for a life-threatening emergency, then head to the nearest ER.")
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
