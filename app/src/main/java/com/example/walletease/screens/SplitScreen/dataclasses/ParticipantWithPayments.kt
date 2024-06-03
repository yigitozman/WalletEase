package com.example.walletease.screens.SplitScreen.dataclasses

data class ParticipantWithPayments(
    val participant: Participant,
    val payments: List<Payment>
)