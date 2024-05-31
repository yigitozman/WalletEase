package com.example.walletease.screens.DashboardScreen.dataclasses

import com.google.firebase.Timestamp

data class Transaction(
    val id: String = "",
    val name: String = "",
    val amount: Float = 0f,
    val date: Timestamp = Timestamp.now(), // Store date as Firestore Timestamp
    val userId: String = ""
)