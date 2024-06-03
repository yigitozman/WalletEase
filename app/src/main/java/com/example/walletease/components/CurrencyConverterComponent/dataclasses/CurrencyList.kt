package com.example.walletease.components.CurrencyConverterComponent.dataclasses

data class Currency(
    val code: String,
    val name: String,
    val symbol: String
)

val predefinedCurrencies = listOf(
    Currency("USD", "United States Dollar", "$"),
    Currency("EUR", "Euro", "€"),
    Currency("GBP", "British Pound", "£"),
    Currency("JPY", "Japanese Yen", "¥"),
    Currency("AUD", "Australian Dollar", "A$"),
    Currency("CAD", "Canadian Dollar", "C$"),
    Currency("CHF", "Swiss Franc", "CHF"),
    Currency("CNY", "Chinese Yuan", "¥"),
    Currency("SEK", "Swedish Krona", "kr"),
    Currency("NZD", "New Zealand Dollar", "NZ$"),
    Currency("TRY", "Turkish Lira", "₺")
)