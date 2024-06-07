package com.example.walletease.components.CurrencyConverterComponent.dataclasses

data class CurrencyConvertResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)