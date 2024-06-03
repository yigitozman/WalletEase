package com.example.walletease.components.DashboardComponent.composables

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.walletease.components.DashboardComponent.dataclass.Transaction
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@Composable
fun TransactionDialog(
    type: String,
    onAddOrUpdateTransaction: (String, Float, Timestamp) -> Unit,
    transaction: Transaction? = null,
    onDismiss: () -> Unit
) {
    var transactionName by rememberSaveable { mutableStateOf(transaction?.name ?: "") }
    var transactionAmount by rememberSaveable { mutableStateOf(transaction?.amount?.toString() ?: "") }
    var transactionDate by rememberSaveable { mutableStateOf(transaction?.date?.toDate()?.let { Date(it.time) } ?: Date()) }
    var amountError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    calendar.time = transactionDate

    fun validateInput(amount: String): Boolean {
        val amountFloat = amount.toFloatOrNull()
        if (amountFloat == null || amountFloat <= 0) {
            amountError = "Enter a valid amount (> 0)"
            return false
        }
        return true
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            transactionDate = calendar.time
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (transaction == null) "Add New $type Transaction" else "Edit $type Transaction",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = transactionName,
                    onValueChange = { transactionName = it },
                    label = { Text("Transaction Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = transactionAmount,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() || char == '.' }) {
                            transactionAmount = it
                            amountError = null
                        }
                    },
                    label = { Text("Transaction Amount") },
                    singleLine = true,
                    isError = amountError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                )

                if (amountError != null) {
                    Text(
                        text = amountError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = "Selected Date: ${SimpleDateFormat("dd/MM/yyyy").format(transactionDate)}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = {
                        datePickerDialog.show() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Select Date")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (validateInput(transactionAmount)) {
                                onAddOrUpdateTransaction(
                                    transactionName,
                                    transactionAmount.toFloat(),
                                    Timestamp(transactionDate)
                                )
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(if (transaction == null) "Add" else "Update")
                    }
                }
            }
        }
    }
}

@Composable
fun FilterDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, Modifier.clickable { expanded = true })
            },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(option)
                    expanded = false },
                    text = { Text(text = option) }
                )
            }
        }
    }
}
