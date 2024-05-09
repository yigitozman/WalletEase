package com.example.walletease

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.walletease.screens.HomeScreen
import com.example.walletease.screens.LoginScreen
import com.example.walletease.screens.SignUpScreen
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

    NavHost(
        navController = navController,
        startDestination = "login_screen"
    ) {
        composable("login_screen") {
            LoginScreen(navController, authViewModel, sharedViewModel)
        }
        composable("signup_screen") {
            SignUpScreen(navController, authViewModel, sharedViewModel)
        }
        composable("home_screen") {
            HomeScreen(navController, authViewModel)
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