package com.example.walletease.components.SplitComponent.dataclasses

data class ParticipantWithPayments(
    val participant: Participant,
    val payments: List<Payment>
)