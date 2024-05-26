package com.example.walletease.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.walletease.dataclasses.predefinedCurrencies
import com.example.walletease.viewmodels.AuthViewModel
import com.example.walletease.viewmodels.CurrencyViewModel

//todo: bottom conversion stays when i change screen but amount on text field goes away
@Composable
fun CurrencyConverterScreen(navController: NavController, authViewModel: AuthViewModel, currencyViewModel: CurrencyViewModel) {
    val user by authViewModel.currentUser.observeAsState()
    val colors = MaterialTheme.colorScheme
    var amount by remember { mutableStateOf(TextFieldValue("")) }
    var baseCurrency by remember { mutableStateOf("USD") }
    var targetCurrency by remember { mutableStateOf("EUR") }
    val conversionResult by currencyViewModel.conversionResult.collectAsState()
    val isFetching by currencyViewModel.isFetching.collectAsState()

    var showBaseCurrencyDialog by remember { mutableStateOf(false) }
    var showTargetCurrencyDialog by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Currency Converter",
                style = MaterialTheme.typography.headlineSmall,
                color = colors.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.text.isEmpty() || it.text.matches(Regex("^\\d*\\.?\\d*\$"))) { amount = it }},
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = { showBaseCurrencyDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
                ) {
                    Text("From: $baseCurrency")
                }

                Button(
                    onClick = { showTargetCurrencyDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
                ) {
                    Text("To: $targetCurrency")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    amount.text.toDoubleOrNull()?.let {
                        currencyViewModel.convertCurrency(it, baseCurrency, targetCurrency)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
            ) {
                Text("Get Exchange Rates")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // This Box will reserve space for the progress indicator or result text
            Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                if (isFetching) {
                    CircularProgressIndicator()
                } else {
                    conversionResult?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.primary,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            if (showBaseCurrencyDialog) {
                CurrencySelectionDialog(
                    currencyList = predefinedCurrencies,
                    onCurrencySelected = { selectedCurrency ->
                        baseCurrency = selectedCurrency
                    },
                    onDismissRequest = { showBaseCurrencyDialog = false }
                )
            }

            if (showTargetCurrencyDialog) {
                CurrencySelectionDialog(
                    currencyList = predefinedCurrencies,
                    onCurrencySelected = { selectedCurrency ->
                        targetCurrency = selectedCurrency
                    },
                    onDismissRequest = { showTargetCurrencyDialog = false }
                )
            }
        }
    }
}