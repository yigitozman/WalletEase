package com.example.walletease.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.walletease.dataclasses.User
import com.example.walletease.viewmodels.AuthViewModel
import com.example.walletease.viewmodels.SharedViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(navController: NavController, authViewModel: AuthViewModel, sharedViewModel: SharedViewModel) {
    MaterialTheme {
        val colors = MaterialTheme.colorScheme

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val focusRequesterPassword = FocusRequester()
        var showError by remember { mutableStateOf(false) }
        var emptyUserError by remember { mutableStateOf(false) }
        var showSuccessMessage by remember { mutableStateOf(false) }

        val successMessage = sharedViewModel.successMessage.collectAsState().value

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
                // Sign Up Title with Material Theming
                Text(
                    text = "Sign Up",
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

                if (emptyUserError) {
                    Text(text = "Username or Password can't be empty.", color = colors.error)
                }

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
                        text = "Account with the same username or password already exists.",
                        color = colors.error
                    )
                }

                if (showSuccessMessage) {
                    navController.previousBackStackEntry?.arguments?.putString("success_message", "Account created successfully!")
                }

                // Sign Up Button with Material Theming
                Button(
                    onClick = {
                        authViewModel.viewModelScope.launch {
                            val existingUser = authViewModel.isUserExists(username, password)
                            if (existingUser) {
                                showError = true
                            } else if (username == "" || password == "") {
                                emptyUserError = true
                            } else {
                                val newUser = User(username = username, password = password)
                                authViewModel.signUp(newUser)
                                sharedViewModel.setSuccessMessage("Account created successfully!")
                                navController.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Sign Up", color = colors.onPrimary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        navController.navigate("login_screen")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Already have an account? Login")
                }
            }
        }
    }
}
