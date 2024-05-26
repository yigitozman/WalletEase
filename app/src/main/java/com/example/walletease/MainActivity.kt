package com.example.walletease

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.walletease.screens.CurrencyConverterScreen
import com.example.walletease.screens.DashboardScreen
import com.example.walletease.screens.ExpenseScreen
import com.example.walletease.screens.IncomeScreen
import com.example.walletease.screens.LoginScreen
import com.example.walletease.screens.ProfileScreen
import com.example.walletease.screens.SignUpScreen
import com.example.walletease.screens.SplitScreen
import com.example.walletease.screens.SubscriptionScreen
import com.example.walletease.sealedclasses.Screens
import com.example.walletease.sealedclasses.items
import com.example.walletease.ui.theme.WalletEaseTheme
import com.example.walletease.viewmodels.AuthViewModel
import com.example.walletease.viewmodels.CurrencyViewModel
import com.example.walletease.viewmodels.SharedViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore

//todo: it closes even if there is more back history at dashboard screen
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        Firebase.firestore
        enableEdgeToEdge()

        val authViewModel: AuthViewModel by viewModels()
        val currencyViewModel: CurrencyViewModel by viewModels()

        setContent {
            WalletEaseTheme {
                val sharedViewModel: SharedViewModel = viewModel()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                    MyApp(authViewModel, sharedViewModel, currencyViewModel)
                }
            }
        }
    }
}

@Composable
fun MyApp(authViewModel: AuthViewModel, sharedViewModel: SharedViewModel, currencyViewModel: CurrencyViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    BackHandler(enabled = currentRoute == Screens.Dashboard.route) {
        if (currentRoute == Screens.Dashboard.route) {
            (context as? Activity)?.finish()
        } else {
            navController.popBackStack()
        }
    }

    LaunchedEffect(currentRoute) {
        selectedItem = when (currentRoute) {
            Screens.Dashboard.route -> 0
            Screens.Subscription.route -> 1
            Screens.CurrencyConverter.route -> 2
            Screens.Split.route -> 3
            Screens.Profile.route -> 4
            else -> 0
        }
    }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                if (currentRoute in listOf(Screens.Dashboard.route, Screens.Subscription.route, Screens.CurrencyConverter.route,
                        Screens.Split.route, Screens.Profile.route)) {
                    NavigationBar {
                        items.forEachIndexed { index, screen ->
                            NavigationBarItem(
                                icon = {
                                    when (screen) {
                                        is Screens.Dashboard -> {
                                            if (currentRoute == screen.route) {
                                                Icon(Icons.Filled.Home, contentDescription = screen.route)
                                            } else {
                                                Icon(Icons.Outlined.Home, contentDescription = screen.route)
                                            }
                                        }
                                        is Screens.Subscription -> {
                                            if (currentRoute == screen.route) {
                                                Icon(painterResource(R.drawable.filled_event_repeat_24), contentDescription = screen.route)
                                            } else {
                                                Icon(painterResource(R.drawable.outline_event_repeat_24), contentDescription = screen.route)
                                            }
                                        }
                                        is Screens.CurrencyConverter -> {
                                            if (currentRoute == screen.route) {
                                                Icon(painterResource(R.drawable.filled_change_circle_24), contentDescription = screen.route)
                                            } else {
                                                Icon(painterResource(R.drawable.outline_change_circle_24), contentDescription = screen.route)
                                            }
                                        }
                                        is Screens.Split -> {
                                            if (currentRoute == screen.route) {
                                                Icon(painterResource(R.drawable.filled_receipt_long_24), contentDescription = screen.route)
                                            } else {
                                                Icon(painterResource(R.drawable.outline_receipt_long_24), contentDescription = screen.route)
                                            }
                                        }
                                        is Screens.Profile -> {
                                            if (currentRoute == screen.route) {
                                                Icon(Icons.Filled.AccountCircle, contentDescription = screen.route)
                                            } else {
                                                Icon(Icons.Outlined.AccountCircle, contentDescription = screen.route)
                                            }
                                        }
                                        else -> { }
                                    }
                                },
                                selected = selectedItem == index,
                                onClick = {
                                    selectedItem = index
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screens.Login.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screens.Login.route) {
                    LoginScreen(navController, authViewModel, sharedViewModel)
                }
                composable(Screens.Signup.route) {
                    SignUpScreen(navController, authViewModel, sharedViewModel)
                }
                composable(Screens.Dashboard.route) {
                    DashboardScreen(navController, authViewModel)
                }
                composable(Screens.Income.route) {
                    IncomeScreen(navController, authViewModel)
                }
                composable(Screens.Expense.route) {
                    ExpenseScreen(navController, authViewModel)
                }
                composable(Screens.Subscription.route) {
                    SubscriptionScreen(navController, authViewModel)
                }
                composable(Screens.CurrencyConverter.route) {
                    CurrencyConverterScreen(navController, authViewModel, currencyViewModel)
                }
                composable(Screens.Split.route) {
                    SplitScreen(navController, authViewModel)
                }
                composable(Screens.Profile.route) {
                    ProfileScreen(navController, authViewModel)
                }
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WalletEaseTheme {
        Greeting("Android")
    }
}
*/