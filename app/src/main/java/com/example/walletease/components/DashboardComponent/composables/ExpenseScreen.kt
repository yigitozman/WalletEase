package com.example.walletease.components.DashboardComponent.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.walletease.components.DashboardComponent.dataclass.Transaction
import com.example.walletease.components.DashboardComponent.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    transactionViewModel: TransactionViewModel = viewModel()
) {
    val expenseTransactions by transactionViewModel.expenseTransactions.observeAsState(initial = listOf())
    var isDialogOpen by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }

    var selectedMonth by remember { mutableStateOf("All") }
    var selectedYear by remember { mutableStateOf("All") }

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val months = listOf("All") + (1..12).map { it.toString() }
    val years = listOf("All") + (currentYear downTo 2000).map { it.toString() }

    val filteredTransactions = expenseTransactions.filter { transaction ->
        (selectedMonth == "All" || SimpleDateFormat("M").format(transaction.date.toDate()) == selectedMonth) &&
                (selectedYear == "All" || SimpleDateFormat("yyyy").format(transaction.date.toDate()) == selectedYear)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense") },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingTransaction = null
                isDialogOpen = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterDropdown(
                    label = "Month",
                    options = months,
                    selectedOption = selectedMonth,
                    onOptionSelected = { selectedMonth = it },
                    modifier = Modifier.weight(1f)
                )
                FilterDropdown(
                    label = "Year",
                    options = years,
                    selectedOption = selectedYear,
                    onOptionSelected = { selectedYear = it },
                    modifier = Modifier.weight(1f)
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredTransactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onEdit = { trans ->
                            editingTransaction = trans
                            isDialogOpen = true
                        },
                        onDelete = { trans ->
                            transactionViewModel.deleteTransaction("expense", trans)
                        }
                    )
                }
            }
        }

        if (isDialogOpen) {
            TransactionDialog(
                type = "Expense",
                onAddOrUpdateTransaction = { name, amount, date ->
                    if (editingTransaction == null) {
                        transactionViewModel.addTransaction("expense", Transaction(name = name, amount = amount, date = date))
                    } else {
                        transactionViewModel.updateTransaction("expense", editingTransaction!!.copy(name = name, amount = amount, date = date))
                    }
                },
                transaction = editingTransaction,
                onDismiss = { isDialogOpen = false }
            )
        }
    }
}