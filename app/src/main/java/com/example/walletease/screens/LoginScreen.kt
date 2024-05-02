package com.example.walletease.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.walletease.viewmodels.AuthViewModel
import com.example.walletease.viewmodels.SharedViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel, sharedViewModel: SharedViewModel) {
    MaterialTheme {
        val colors = MaterialTheme.colorScheme

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val focusRequesterPassword = FocusRequester()
        var showError by remember { mutableStateOf(false) }

        val successMessage = sharedViewModel.successMessage.collectAsState().value

        DisposableEffect(username, password) {
            onDispose {
                sharedViewModel.setSuccessMessage("")
            }
        }

        if (successMessage?.isNotEmpty() == true) {
            AlertDialog(
                onDismissRequest = {
                    sharedViewModel.setSuccessMessage("")
                },
                title = {
                    Text(text = "Success")
                },
                text = {
                    Text(text = successMessage)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            sharedViewModel.setSuccessMessage("")
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        Card(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Login Title with Material Theming
                Text(
                    text = "Login",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
                Spacer(modifier = Modifier.height(15.dp))

                // Username Field with rounded corners and filled background
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusRequesterPassword.requestFocus()
                        }
                    ),
                    label = { Text("Username", style = MaterialTheme.typography.bodyMedium) },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge
                )

                // Password Field with rounded corners and filled background
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    label = { Text("Password", style = MaterialTheme.typography.bodyMedium) },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    textStyle = MaterialTheme.typography.bodyLarge
                )

                if (showError) {
                    Text(
                        text = "Username or Password is wrong. Try again.",
                        color = colors.error
                    )
                }

                // Login Button with Material Theming
                Button(
                    onClick = {
                        authViewModel.viewModelScope.launch {
                            val user = authViewModel.login(username, password)
                            if (user != null) {
                                authViewModel.setUser(user)
                                navController.navigate("home_screen")
                            } else {
                                showError = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Login", color = colors.onPrimary)
                }


                Spacer(modifier = Modifier.height(8.dp))

                // Sign Up Button with Material Theming
                Button(
                    onClick = {
                        navController.navigate("signup_screen")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Don't have an account? Sign up", color = colors.onPrimary)
                }
            }
        }
    }
}