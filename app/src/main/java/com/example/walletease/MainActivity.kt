package com.example.walletease

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.walletease.screens.HomeScreen
import com.example.walletease.screens.LoginScreen
import com.example.walletease.screens.ProfileScreen
import com.example.walletease.screens.SignUpScreen
import com.example.walletease.sealedclasses.Screens
import com.example.walletease.sealedclasses.items
import com.example.walletease.ui.theme.WalletEaseTheme
import com.example.walletease.viewmodels.AuthViewModel
import com.example.walletease.viewmodels.SharedViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        val authViewModel: AuthViewModel by viewModels()

        setContent {
            WalletEaseTheme {
                val sharedViewModel: SharedViewModel = viewModel()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                    MyApp(authViewModel, sharedViewModel)
                }
            }
        }
    }
}

@Composable
fun MyApp(authViewModel: AuthViewModel, sharedViewModel: SharedViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                if (currentRoute in listOf(Screens.Home.route, Screens.Profile.route)) {
                    NavigationBar {
                        items.forEachIndexed { index, screen ->
                            NavigationBarItem(
                                icon = {
                                    when (screen) {
                                        is Screens.Home -> {
                                            if (currentRoute == screen.route) {
                                                Icon(Icons.Filled.Home, contentDescription = screen.route)
                                            } else {
                                                Icon(Icons.Outlined.Home, contentDescription = screen.route)
                                            }
                                        }
                                        is Screens.Profile -> {
                                            if (currentRoute == screen.route) {
                                                Icon(Icons.Filled.AccountCircle, contentDescription = screen.route)
                                            } else {
                                                Icon(Icons.Outlined.AccountCircle, contentDescription = screen.route)
                                            }
                                        }
                                        Screens.Login -> TODO()
                                        Screens.Signup -> TODO()
                                    }
                                },
                                selected = selectedItem == index,
                                onClick = {
                                    selectedItem = index
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
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
                composable(Screens.Home.route) {
                    HomeScreen(navController, authViewModel)
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