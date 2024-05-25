package com.example.walletease.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.walletease.R
import com.example.walletease.sealedclasses.Screens
import com.example.walletease.viewmodels.AuthViewModel
import com.hd.charts.PieChartView
import com.hd.charts.common.model.ChartDataSet
import com.hd.charts.style.ChartViewDefaults
import com.hd.charts.style.ChartViewStyle
import com.hd.charts.style.PieChartDefaults

@Composable
fun DashboardScreen(navController: NavController, authViewModel: AuthViewModel) {
    val user by authViewModel.currentUser.observeAsState()
    val colors = MaterialTheme.colorScheme
    var incomeExpandedState by remember { mutableStateOf(false) }
    var expenseExpandedState by remember { mutableStateOf(false) }

    //todo: add a pie / donut chart and change subscriptions part to modify subscriptions, also make cards expandable cards that will show last 5 income or expense and a button to show all that will go to the wanted page

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val pieColors = listOf(
            Color.Green,
            Color.Red
        )

        val style = PieChartDefaults.style(
            borderColor = colors.background,
            donutPercentage = 40f,
            borderWidth = 6f,
            pieColors = pieColors,
            chartViewStyle = custom(width = 300.dp)
        )

        val dataSet = ChartDataSet(
            items = listOf(5f, 10f),
            title = "Monthly Chart",
            prefix = "Category ",
            postfix = " $"
        )

        PieChartView(dataSet = dataSet, style = style)

        Text(text = "Balance: 1000", modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(bottom = 15.dp),
            style = MaterialTheme.typography.headlineSmall)

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp),
                onClick = { navController.navigate(Screens.Income.route) },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_attach_money_24),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Income", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                }
            }
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp),
                onClick = { navController.navigate(Screens.Expense.route) },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF44336)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_money_off_24),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Expense", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun custom(width: Dp = Dp.Infinity): ChartViewStyle {
    val colors = MaterialTheme.colorScheme

    return ChartViewDefaults.style(
        width = width,
        cornerRadius = 10.dp,
        backgroundColor = colors.background,
        shadow = 0.dp)
}


