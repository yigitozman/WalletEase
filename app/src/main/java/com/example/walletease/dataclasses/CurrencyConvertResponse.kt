package com.example.walletease.dataclasses

data class CurrencyConvertResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)