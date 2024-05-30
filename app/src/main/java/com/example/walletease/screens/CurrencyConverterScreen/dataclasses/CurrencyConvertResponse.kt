package com.example.walletease.screens.CurrencyConverterScreen.dataclasses

data class CurrencyConvertResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)