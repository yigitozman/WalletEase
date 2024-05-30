package com.example.walletease.screens.CurrencyConverterScreen.dialogscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.walletease.screens.CurrencyConverterScreen.dataclasses.Currency

@Composable
fun CurrencySelectionDialog(
    currencyList: List<Currency>,
    onCurrencySelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredCurrencies = currencyList.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.code.contains(searchQuery, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Select Currency") },
        text = {
            Column {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.padding(4.dp)) {
                            if (searchQuery.isEmpty()) {
                                Text(text = "Search...")
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(modifier = Modifier.fillMaxHeight(0.6f)) {
                    filteredCurrencies.forEach { currency ->
                        TextButton(onClick = {
                            onCurrencySelected(currency.code)
                            onDismissRequest()
                        }) {
                            Text(text = "${currency.code} - ${currency.name} (${currency.symbol})")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}