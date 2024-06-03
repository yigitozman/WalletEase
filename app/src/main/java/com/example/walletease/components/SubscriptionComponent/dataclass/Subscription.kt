package com.example.walletease.components.SubscriptionComponent.dataclass

data class Subscription(
    val id: String = "",
    val name: String = "",
    val paymentDate: Int = 0,
    val subscriptionPrice: Float = 0f,
    val userId: String = ""
)