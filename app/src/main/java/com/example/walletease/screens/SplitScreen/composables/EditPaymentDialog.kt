package com.example.walletease.screens.SplitScreen.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.walletease.screens.SplitScreen.dataclasses.Payment

@Composable
fun EditPaymentDialog(
    payment: Payment,
    allParticipants: List<String>,
    onDismiss: () -> Unit,
    onSave: (String, Double, List<String>) -> Unit
) {
    var newDescription by remember { mutableStateOf(payment.description) }
    var newAmount by remember { mutableStateOf(payment.amount.toString()) }
    val excludedParticipants = remember { mutableStateListOf<String>().also { it.addAll(payment.excludedParticipants) } }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edit Payment") },
        confirmButton = {
            Button(onClick = {
                onSave(newDescription, newAmount.toDoubleOrNull() ?: 0.0, excludedParticipants.toList())
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = newDescription,
                    onValueChange = { newDescription = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text("Description") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                OutlinedTextField(
                    value = newAmount,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() || char == '.' }) {
                            newAmount = it
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Exclude Participants:", style = MaterialTheme.typography.bodyLarge)

                allParticipants.forEach { participant ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = participant in excludedParticipants,
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    excludedParticipants.add(participant)
                                } else {
                                    excludedParticipants.remove(participant)
                                }
                            }
                        )
                        Text(participant, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    )
}