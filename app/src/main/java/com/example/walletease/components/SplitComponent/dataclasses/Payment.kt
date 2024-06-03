package com.example.walletease.components.SplitComponent.dataclasses

data class Payment(
    val description: String,
    val amount: Double,
    val excludedParticipants: List<String> = listOf()
)