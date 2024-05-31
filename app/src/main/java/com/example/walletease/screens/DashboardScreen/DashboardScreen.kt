package com.example.walletease.screens.DashboardScreen

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.walletease.R
import com.example.walletease.screens.DashboardScreen.viewmodel.TransactionViewModel
import com.example.walletease.sealedclasses.Screens
import com.hd.charts.PieChartView
import com.hd.charts.common.model.ChartDataSet
import com.hd.charts.style.ChartViewDefaults
import com.hd.charts.style.ChartViewStyle
import com.hd.charts.style.PieChartDefaults
import java.util.Calendar

//todo: when i open the app logged in i see the no data available text before the chart appears
@Composable
fun DashboardScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel = viewModel()
) {
    val incomeTransactions by transactionViewModel.incomeTransactions.observeAsState(initial = listOf())
    val expenseTransactions by transactionViewModel.expenseTransactions.observeAsState(initial = listOf())

    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    val lastMonthIncome = incomeTransactions.filter {
        val date = it.date.toDate()
        val cal = Calendar.getInstance().apply { time = date }
        cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
    }

    val lastMonthExpense = expenseTransactions.filter {
        val date = it.date.toDate()
        val cal = Calendar.getInstance().apply { time = date }
        cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
    }

    val totalIncome = lastMonthIncome.fold(0f) { sum, transaction -> sum + transaction.amount }
    val totalExpense = lastMonthExpense.fold(0f) { sum, transaction -> sum + transaction.amount }

    Scaffold() { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (totalIncome > 0 || totalExpense > 0) {
                val pieColors = listOf(
                    Color(0xFF4CAF50),
                    Color(0xFFF44336)
                )

                val style = PieChartDefaults.style(
                    borderColor = MaterialTheme.colorScheme.background,
                    donutPercentage = 40f,
                    borderWidth = 6f,
                    pieColors = pieColors,
                    chartViewStyle = custom(width = 300.dp)
                )

                val dataSet = ChartDataSet(
                    items = listOf(totalIncome, totalExpense),
                    title = "${currentMonth + 1}/${currentYear} Transactions Chart",
                    prefix = "",
                    postfix = ""
                )

                PieChartView(dataSet = dataSet, style = style)
            }
            else {
                Text(text = "No Data Available for the pie chart for ${currentMonth + 1}/${currentYear}", style = MaterialTheme.typography.headlineSmall)
            }

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


