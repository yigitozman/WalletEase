package com.example.walletease.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.walletease.sealedclasses.Screens
import com.example.walletease.viewmodels.AuthViewModel

@Composable
fun DashboardScreen(navController: NavController, authViewModel: AuthViewModel) {
    val user by authViewModel.currentUser.observeAsState()
    val colors = MaterialTheme.colorScheme
    var incomeExpandedState by remember { mutableStateOf(false) }
    var expenseExpandedState by remember { mutableStateOf(false) }

    //todo: add a pie / donut chart and change subscriptions part to modify subscriptions, also make cards expandable cards that will show last 5 income or expense and a button to show all that will go to the wanted page

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Balance: 1000", modifier = Modifier.align(Alignment.CenterHorizontally).padding(15.dp),)

        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = { incomeExpandedState = !incomeExpandedState }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "Monthly Income: 2000", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (incomeExpandedState) {
                        for (i in 1..5) {
                            Text(text = "Income Category $i: 2000")
                        }
                        Button(
                            onClick = { navController.navigate(Screens.Income.route) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(text = "View More")
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = { expenseExpandedState = !expenseExpandedState }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "Monthly Expense: 1000", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (expenseExpandedState) {
                        for (i in 1..5) {
                            Text(text = "Expense Category $i: 1000")
                        }
                        Button(
                            onClick = { navController.navigate(Screens.Expense.route) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(text = "View More")
                        }
                    }
                }
            }
        }
    }
}


