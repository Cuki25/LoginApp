package com.example.loginapp.ui.signup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.loginapp.data.SignUpDraft
import com.example.loginapp.ui.components.EditableDropdown
import com.example.loginapp.ui.components.LabeledDropdown

// Option lists for the medical profile dropdowns.
val genderOptions = listOf(
    "Male",
    "Female",
    "Intersex",
    "Prefer not to say"
)

val bloodTypeOptions = listOf(
    "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "Unknown"
)

val commonAllergies = listOf(
    "Peanuts",
    "Tree Nuts (Almonds, Walnuts, Cashews)",
    "Milk / Dairy",
    "Eggs",
    "Shellfish (Shrimp, Crab, Lobster)",
    "Fish",
    "Wheat / Gluten",
    "Soy",
    "Penicillin / Amoxicillin",
    "Aspirin / NSAIDs",
    "Sulfa Drugs",
    "Latex",
    "Insect Stings (Bees, Wasps)",
    "Dust Mites",
    "Pollen / Seasonal Allergies"
)

@Composable
fun MedicalProfileScreen(
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    var gender by remember { mutableStateOf(SignUpDraft.gender) }
    var bloodType by remember { mutableStateOf(SignUpDraft.bloodType) }
    var allergies by remember { mutableStateOf(SignUpDraft.allergies) }
    var weight by remember { mutableStateOf(SignUpDraft.weight) }
    var height by remember { mutableStateOf(SignUpDraft.height) }

    // Dynamic lists. Start with one empty entry each so there's a field to type in.
    val conditions = remember {
        mutableStateListOf<String>().apply {
            if (SignUpDraft.conditions.isEmpty()) add("") else addAll(SignUpDraft.conditions)
        }
    }
    val medications = remember {
        mutableStateListOf<String>().apply {
            if (SignUpDraft.medications.isEmpty()) add("") else addAll(SignUpDraft.medications)
        }
    }

    fun persist() {
        SignUpDraft.gender = gender
        SignUpDraft.bloodType = bloodType
        SignUpDraft.allergies = allergies
        SignUpDraft.weight = weight
        SignUpDraft.height = height
        SignUpDraft.conditions = conditions.map { it.trim() }.filter { it.isNotEmpty() }
        SignUpDraft.medications = medications.map { it.trim() }.filter { it.isNotEmpty() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text("Medical profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "Help us keep you safe. You can skip and add this later.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(24.dp))

        LabeledDropdown(
            label = "Gender",
            options = genderOptions,
            selected = gender,
            onSelected = { gender = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        LabeledDropdown(
            label = "Blood Type",
            options = bloodTypeOptions,
            selected = bloodType,
            onSelected = { bloodType = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // Allergies: pick a common allergy from the list OR type a custom one.
        EditableDropdown(
            label = "Allergies",
            options = commonAllergies,
            value = allergies,
            onValueChange = { allergies = it },
            placeholder = "Select a common allergy or type your own",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(12.dp))
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Height (cm)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        DynamicFieldList(
            title = "Any conditions?",
            entries = conditions,
            entryLabel = "Condition",
            addLabel = "Add more"
        )

        Spacer(Modifier.height(24.dp))

        DynamicFieldList(
            title = "On any medication?",
            entries = medications,
            entryLabel = "Medication",
            addLabel = "Add more"
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { persist(); onContinue() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
            Text("Skip for now")
        }
        Spacer(Modifier.height(24.dp))
    }
}

/**
 * A reusable block that renders a titled list of text fields with a remove
 * button per row and an "Add more" button that appends a new empty field.
 *
 * [entries] is a SnapshotStateList so edits/adds/removes recompose in place.
 */
@Composable
fun DynamicFieldList(
    title: String,
    entries: MutableList<String>,
    entryLabel: String,
    addLabel: String
) {
    Column(Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        entries.forEachIndexed { index, value ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = { entries[index] = it },
                    label = { Text("$entryLabel ${index + 1}") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                // Allow removing any row as long as at least one remains.
                if (entries.size > 1) {
                    IconButton(onClick = { entries.removeAt(index) }) {
                        Icon(Icons.Filled.Close, contentDescription = "Remove")
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        OutlinedButton(
            onClick = { entries.add("") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(addLabel)
        }
    }
}
