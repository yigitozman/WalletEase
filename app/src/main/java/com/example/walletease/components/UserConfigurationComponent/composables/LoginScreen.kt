package com.example.walletease.components.UserConfigurationComponent.composables

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.walletease.R
import com.example.walletease.components.UserConfigurationComponent.viewmodel.AuthViewModel
import com.example.walletease.sealedclasses.Screens
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    authViewModel.clearError()

    val colors = MaterialTheme.colorScheme
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val focusRequesterPassword = FocusRequester()
    val showError by authViewModel.showError.observeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.loginanimation)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    BackHandler {
        (context as? Activity)?.finish()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = colors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll((rememberScrollState())),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                composition = composition,
                progress = {
                    progress
                },
                modifier = Modifier.size(240.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusRequesterPassword.requestFocus()
                    }
                ),
                label = { Text("Email", style = MaterialTheme.typography.bodyMedium) },
                modifier = Modifier
                    .fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it.replace(" ", "") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                label = { Text("Password", style = MaterialTheme.typography.bodyMedium) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequesterPassword),
                visualTransformation = PasswordVisualTransformation(),
                textStyle = MaterialTheme.typography.bodyLarge
            )

            if (showError != null && showError!!.isNotEmpty()) {
                Text(
                    text = showError!!,
                    color = colors.error
                )
            }

            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        authViewModel.setError("Email or Password is empty.")
                    }
                    else {
                        authViewModel.viewModelScope.launch {
                            authViewModel.login(email, password).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    keyboardController?.hide()
                                    navController.navigate(Screens.Dashboard.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
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

            Button(
                onClick = {
                    keyboardController?.hide()
                    navController.navigate(Screens.Signup.route)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
            ) {
                Text("Don't have an account? Sign up", color = colors.primary)
            }
        }
    }
}