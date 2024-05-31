package com.example.walletease.screens.SubscriptionScreen.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.walletease.screens.SubscriptionScreen.dataclass.Subscription

@Composable
fun SubscriptionDialog(
    onAddOrUpdateSubscription: (String, Int, Float) -> Unit,
    subscription: Subscription? = null,
    onDismiss: () -> Unit
) {
    var subscriptionName by rememberSaveable { mutableStateOf(subscription?.name ?: "") }
    var paymentDate by rememberSaveable { mutableStateOf(subscription?.paymentDate?.toString() ?: "") }
    var subscriptionPrice by rememberSaveable { mutableStateOf(subscription?.subscriptionPrice?.toString() ?: "") }

    var paymentDateError by remember { mutableStateOf<String?>(null) }
    var subscriptionPriceError by remember { mutableStateOf<String?>(null) }

    fun validateInput(paymentDate: String, subscriptionPrice: String): Boolean {
        val paymentDateInt = paymentDate.toIntOrNull()
        if (paymentDateInt == null || paymentDateInt !in 1..31) {
            paymentDateError = "Enter a valid date (1-31)"
            return false
        }

        val subscriptionPriceFloat = subscriptionPrice.toFloatOrNull()
        if (subscriptionPriceFloat == null || subscriptionPriceFloat <= 0) {
            subscriptionPriceError = "Enter a valid price (> 0)"
            return false
        }

        return true
    }

    Dialog(onDismissRequest = { onDismiss() }) {
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
                    text = if (subscription == null) "Add New Subscription" else "Edit Subscription",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = subscriptionName,
                    onValueChange = { subscriptionName = it },
                    label = { Text("Subscription Name") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = paymentDate,
                    onValueChange = {
                        paymentDate = it
                        paymentDateError = null
                    },
                    label = { Text("Payment Date (e.g., '15')") },
                    singleLine = true,
                    isError = paymentDateError != null,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                paymentDateError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = subscriptionPrice,
                    onValueChange = {
                        subscriptionPrice = it
                        subscriptionPriceError = null
                    },
                    label = { Text("Subscription Price") },
                    singleLine = true,
                    isError = subscriptionPriceError != null,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                subscriptionPriceError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (validateInput(paymentDate, subscriptionPrice)) {
                                onAddOrUpdateSubscription(
                                    subscriptionName,
                                    paymentDate.toInt(),
                                    subscriptionPrice.toFloat()
                                )
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(if (subscription == null) "Add" else "Update")
                    }
                }
            }
        }
    }
}