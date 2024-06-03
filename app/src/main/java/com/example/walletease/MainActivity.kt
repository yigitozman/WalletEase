package com.example.walletease

import android.app.Activity
import android.os.Bundle
import android.util.Log
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
import com.example.walletease.components.CurrencyConverterComponent.composables.CurrencyConverterScreen
import com.example.walletease.components.CurrencyConverterComponent.viewmodel.CurrencyViewModel
import com.example.walletease.components.DashboardComponent.composables.DashboardScreen
import com.example.walletease.components.DashboardComponent.composables.ExpenseScreen
import com.example.walletease.components.DashboardComponent.composables.IncomeScreen
import com.example.walletease.components.DashboardComponent.viewmodel.TransactionViewModel
import com.example.walletease.components.SplitComponent.composables.SplitScreen
import com.example.walletease.components.SubscriptionComponent.composables.SubscriptionScreen
import com.example.walletease.components.SubscriptionComponent.viewmodel.SubscriptionViewModel
import com.example.walletease.components.UserConfigurationComponent.composables.LoginScreen
import com.example.walletease.components.UserConfigurationComponent.composables.ProfileScreen
import com.example.walletease.components.UserConfigurationComponent.composables.SignUpScreen
import com.example.walletease.components.UserConfigurationComponent.viewmodel.AuthViewModel
import com.example.walletease.components.UserConfigurationComponent.viewmodel.SharedViewModel
import com.example.walletease.sealedclasses.Screens
import com.example.walletease.sealedclasses.items
import com.example.walletease.ui.theme.WalletEaseTheme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging

//todo: fix amount parts according to be valid currency
class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        Firebase.firestore
        enableEdgeToEdge()

        fetchAndSaveFcmToken()

        auth = Firebase.auth
        val authViewModel: AuthViewModel by viewModels()
        val currencyViewModel: CurrencyViewModel by viewModels()
        val subscriptionViewModel: SubscriptionViewModel by viewModels()
        val transactionViewModel: TransactionViewModel by viewModels()

        setContent {
            WalletEaseTheme {
                val sharedViewModel: SharedViewModel = viewModel()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                    MyApp(authViewModel, sharedViewModel, currencyViewModel, subscriptionViewModel, transactionViewModel)
                }
            }
        }
    }
}

fun fetchAndSaveFcmToken() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
            return@addOnCompleteListener
        }

        val token = task.result
        saveTokenToFirestore(token)
    }
}

private fun saveTokenToFirestore(token: String) {
    val user = FirebaseAuth.getInstance().currentUser
    user?.let {
        val db = FirebaseFirestore.getInstance()
        db.collection("subscriptions")
            .whereEqualTo("userId", it.uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    db.collection("subscriptions").document(document.id)
                        .update("fcmToken", token)
                        .addOnSuccessListener { Log.d("MainActivity", "Token saved successfully to subscription: ${document.id}") }
                        .addOnFailureListener { e -> Log.w("MainActivity", "Error saving token to subscription: ${document.id}", e) }
                }
            }
            .addOnFailureListener { e ->
                Log.w("MainActivity", "Error fetching subscriptions", e)
            }
    }
}

@Composable
fun MyApp(authViewModel: AuthViewModel, sharedViewModel: SharedViewModel, currencyViewModel: CurrencyViewModel, subscriptionViewModel: SubscriptionViewModel, transactionViewModel: TransactionViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    var startDestination: String

    val user = Firebase.auth.currentUser

    if (user != null) {
        startDestination = Screens.Dashboard.route
    } else {
        startDestination = Screens.Login.route
    }

    BackHandler(enabled = currentRoute == Screens.Login.route) {
        (context as? Activity)?.finish()
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
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screens.Login.route) {
                    LoginScreen(navController, authViewModel, sharedViewModel)
                }
                composable(Screens.Signup.route) {
                    SignUpScreen(navController, authViewModel, sharedViewModel)
                }
                composable(Screens.Dashboard.route) {
                    DashboardScreen(navController)
                }
                composable(Screens.Income.route) {
                    IncomeScreen(transactionViewModel)
                }
                composable(Screens.Expense.route) {
                    ExpenseScreen(transactionViewModel)
                }
                composable(Screens.Subscription.route) {
                    SubscriptionScreen(subscriptionViewModel)
                }
                composable(Screens.CurrencyConverter.route) {
                    CurrencyConverterScreen(currencyViewModel)
                }
                composable(Screens.Split.route) {
                    SplitScreen()
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