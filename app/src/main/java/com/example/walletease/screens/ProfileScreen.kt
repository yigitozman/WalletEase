package com.example.walletease.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.walletease.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel) {
    val user by authViewModel.currentUser.observeAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Hello, ${user?.email}")
                Button(onClick = {
                    authViewModel.viewModelScope.launch {
                        FirebaseAuth.getInstance().signOut()
                        authViewModel.clearError()
                        navController.popBackStack()
                        authViewModel.setUser(null)
                    }
                }) {
                    Text("Sign Out")
                }
            }
        }
    }
}