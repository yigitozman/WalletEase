package com.example.walletease.screens.SplitScreen.dataclasses

data class Payment(
    val description: String,
    val amount: Double,
    val excludedParticipants: List<String> = listOf()
)