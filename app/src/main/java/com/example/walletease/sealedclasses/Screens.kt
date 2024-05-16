package com.example.walletease.sealedclasses

sealed class Screens(val route: String, val label: String) {
    data object Home : Screens("home_screen", "Home")
    data object Profile : Screens("profile_screen", "Profile")
    data object Login : Screens("login_screen", "Login")
    data object Signup : Screens("signup_screen", "Signup")
}

val items = listOf(
    Screens.Home,
    Screens.Profile
)