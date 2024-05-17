package com.example.walletease.sealedclasses

sealed class Screens(val route: String, val label: String) {
    data object Login : Screens("login_screen", "Login")
    data object Signup : Screens("signup_screen", "Signup")
    data object Dashboard : Screens("dashboard_screen", "Dashboard")
    data object Income : Screens("income_screen", "Incomes")
    data object Expense : Screens("expense_screen", "Expenses")
    data object Subscription : Screens("subscription_screen", "Subscriptions")
    data object CurrencyConverter : Screens("currency_converter_screen", "Currency Converter")
    data object Split : Screens("split_screen", "Split")
    data object Profile : Screens("profile_screen", "Profile")
}

val items = listOf(
    Screens.Dashboard,
    Screens.Subscription,
    Screens.CurrencyConverter,
    Screens.Split,
    Screens.Profile
)