package com.example.walletease.components.SplitComponent.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.walletease.components.SplitComponent.dataclasses.Payment

@Composable
fun PaymentsInput(
    payments: List<Payment>,
    onPaymentsChange: (List<Payment>) -> Unit,
    onEditPayment: (Int) -> Unit
) {
    var newPaymentDescription by remember { mutableStateOf("") }
    var newPaymentAmount by remember { mutableStateOf("") }

    Column {
        payments.forEachIndexed { index, payment ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = payment.description,
                    onValueChange = { newDescription ->
                        val newPayments = payments.toMutableList()
                        newPayments[index] = payment.copy(description = newDescription)
                        onPaymentsChange(newPayments)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    enabled = false
                )
                OutlinedTextField(
                    value = payment.amount.toString(),
                    onValueChange = { newAmount ->
                        val newPayments = payments.toMutableList()
                        newPayments[index] = payment.copy(amount = newAmount.toDoubleOrNull() ?: 0.0)
                        onPaymentsChange(newPayments)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    enabled = false
                )
                IconButton(onClick = { onEditPayment(index) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Payment")
                }
                IconButton(onClick = {
                    val newPayments = payments.toMutableList().also { it.removeAt(index) }
                    onPaymentsChange(newPayments)
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Payment")
                }
            }
        }

        OutlinedTextField(
            value = newPaymentDescription,
            onValueChange = { newPaymentDescription = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            placeholder = { Text("New payment description") },
            label = { Text("Description") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        OutlinedTextField(
            value = newPaymentAmount,
            onValueChange = {
                if (it.all { char -> char.isDigit() || char == '.' }) {
                    newPaymentAmount = it
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            placeholder = { Text("New payment amount") },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
        )

        Button(
            onClick = {
                val newPayment = Payment(
                    description = newPaymentDescription,
                    amount = newPaymentAmount.toDoubleOrNull() ?: 0.0
                )
                onPaymentsChange(payments + newPayment)
                newPaymentDescription = ""
                newPaymentAmount = ""
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Payment")
        }
    }
}