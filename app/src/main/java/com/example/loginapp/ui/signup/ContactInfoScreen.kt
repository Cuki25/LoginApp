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
import androidx.compose.material3.Card
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
import com.example.loginapp.data.EmergencyContact
import com.example.loginapp.data.SignUpDraft

/** Mutable UI model for one contact block. */
private class ContactState(name: String = "", number: String = "", address: String = "") {
    var name by mutableStateOf(name)
    var number by mutableStateOf(number)
    var address by mutableStateOf(address)

    fun toModel() = EmergencyContact(name.trim(), number.trim(), address.trim())
    fun isNotEmpty() = name.isNotBlank() || number.isNotBlank() || address.isNotBlank()
}

@Composable
fun ContactInfoScreen(
    onFinish: () -> Unit,
    onSkip: () -> Unit
) {
    val mother = remember {
        ContactState(SignUpDraft.mother.name, SignUpDraft.mother.number, SignUpDraft.mother.address)
    }
    val father = remember {
        ContactState(SignUpDraft.father.name, SignUpDraft.father.number, SignUpDraft.father.address)
    }
    val extras = remember {
        mutableStateListOf<ContactState>().apply {
            SignUpDraft.extraContacts.forEach { add(ContactState(it.name, it.number, it.address)) }
        }
    }

    fun persist() {
        SignUpDraft.mother = mother.toModel()
        SignUpDraft.father = father.toModel()
        SignUpDraft.extraContacts = extras.filter { it.isNotEmpty() }.map { it.toModel() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text("Contact information", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "Emergency contacts. You can skip and add these later.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(24.dp))

        ContactBlock(title = "Mother's Info", state = mother)

        Spacer(Modifier.height(16.dp))

        ContactBlock(title = "Father's Info", state = father)

        extras.forEachIndexed { index, contact ->
            Spacer(Modifier.height(16.dp))
            ContactBlock(
                title = "Emergency Contact ${index + 1}",
                state = contact,
                onRemove = { extras.removeAt(index) }
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = { extras.add(ContactState()) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Add more emergency contact")
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { persist(); onFinish() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Finish")
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
            Text("Skip for now")
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ContactBlock(
    title: String,
    state: ContactState,
    onRemove: (() -> Unit)? = null
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (onRemove != null) {
                    IconButton(onClick = onRemove) {
                        Icon(Icons.Filled.Close, contentDescription = "Remove contact")
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.name,
                onValueChange = { state.name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.number,
                onValueChange = { state.number = it },
                label = { Text("Number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.address,
                onValueChange = { state.address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
