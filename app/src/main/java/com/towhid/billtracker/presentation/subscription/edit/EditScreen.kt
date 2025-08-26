package com.towhid.billtracker.presentation.subscription.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.towhid.billtracker.domain.model.BillingCycleType
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(id: Long, onDone: () -> Unit) {
    val viewModel: EditViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(id) { viewModel.onEvent(EditEvent.Load(id)) }

    Scaffold(topBar = { TopAppBar(title = { Text(if (id == -1L) "Add" else "Edit") }) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(EditEvent.NameChanged(it)) },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.amount,
                onValueChange = { viewModel.onEvent(EditEvent.AmountChanged(it)) },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.currency,
                onValueChange = { viewModel.onEvent(EditEvent.CurrencyChanged(it)) },
                label = { Text("Currency") },
                modifier = Modifier.fillMaxWidth()
            )

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    readOnly = true,
                    value = state.cycle.name,
                    onValueChange = {},
                    label = { Text("Billing cycle") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    BillingCycleType.entries.forEach { type ->
                        DropdownMenuItem(text = { Text(type.name) }, onClick = {
                            viewModel.onEvent(EditEvent.CycleChanged(type))
                            expanded = false
                        })
                    }
                }
            }

            if (state.cycle == BillingCycleType.CUSTOM) {
                OutlinedTextField(
                    value = state.customDays,
                    onValueChange = { viewModel.onEvent(EditEvent.CustomDaysChanged(it)) },
                    label = { Text("Custom days") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = state.nextDue,
                onValueChange = { viewModel.onEvent(EditEvent.NextDueChanged(it)) },
                label = { Text("Next due (yyyy-MM-dd)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.notes,
                onValueChange = { viewModel.onEvent(EditEvent.NotesChanged(it)) },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            if (state.error != null) Text(state.error!!, color = MaterialTheme.colorScheme.error)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { viewModel.onEvent(EditEvent.Save); onDone() },
                    enabled = state.isValid && !state.isSaving
                ) { Text("Save") }
                OutlinedButton(onClick = onDone) { Text("Cancel") }
            }
        }
    }
}