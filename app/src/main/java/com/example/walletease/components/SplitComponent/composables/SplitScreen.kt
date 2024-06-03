package com.example.walletease.components.SplitComponent.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.walletease.R
import com.example.walletease.components.SplitComponent.dataclasses.Participant
import com.example.walletease.components.SplitComponent.dataclasses.ParticipantWithPayments
import com.example.walletease.components.SplitComponent.dataclasses.Settlement
import java.util.Locale

@Composable
fun SplitScreen() {
    var participantCount by rememberSaveable { mutableStateOf(0) }
    var participants by rememberSaveable { mutableStateOf(listOf<ParticipantWithPayments>()) }
    var showSettlementsDialog by rememberSaveable { mutableStateOf(false) }
    var showEditPaymentsDialog by rememberSaveable { mutableStateOf(false) }
    var settlements by rememberSaveable { mutableStateOf(listOf<Settlement>()) }
    var editingParticipantIndex by rememberSaveable { mutableStateOf(-1) }
    var editingPaymentIndex by rememberSaveable { mutableStateOf(-1) }

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.splitscreenanimation)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Scaffold(
        floatingActionButton = {
            if (participants.isNotEmpty()) {
                FloatingActionButton(onClick = {
                    settlements = calculateSettlements(participants)
                    showSettlementsDialog = true
                }) {
                    Icon(painterResource(R.drawable.baseline_calculate_24), contentDescription = "Calculate Settlements")
                }
            }
        },
        content = { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (participants.isEmpty()) {

                        LottieAnimation(
                            composition = composition,
                            progress = {
                                progress
                            },
                            modifier = Modifier.size(240.dp)
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            IconButton(onClick = {
                                if (participantCount > 0) {
                                    participantCount -= 1
                                }
                            }) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Remove Participant")
                            }
                            OutlinedTextField(
                                value = participantCount.toString(),
                                onValueChange = { participantCount = it.toIntOrNull() ?: 0 },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                enabled = false,
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.inverseSurface,
                                    textAlign = TextAlign.Center
                                )
                            )
                            IconButton(onClick = {
                                participantCount += 1
                            }) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Add Participant")
                            }
                        }

                        Spacer(modifier = Modifier.height(15.dp))

                        Button(
                            onClick = {
                                participants = List(participantCount) { ParticipantWithPayments(
                                    Participant("Participant ${it + 1}"), mutableListOf()) }
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Set Participants")
                        }
                    } else {
                        ParticipantsInput(participants, { newParticipants ->
                            participants = newParticipants
                        }, { participantIndex, paymentIndex ->
                            editingParticipantIndex = participantIndex
                            editingPaymentIndex = paymentIndex
                            showEditPaymentsDialog = true
                        })

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (showEditPaymentsDialog) {
                        if (editingParticipantIndex >= 0 && editingPaymentIndex >= 0) {
                            val participant = participants[editingParticipantIndex]
                            val payment = participant.payments[editingPaymentIndex]
                            val allParticipantNames = participants.map { it.participant.name }
                            EditPaymentDialog(
                                payment = payment,
                                allParticipants = allParticipantNames,
                                onDismiss = { showEditPaymentsDialog = false },
                                onSave = { newDescription, newAmount, excludedParticipants ->
                                    val newParticipants = participants.toMutableList()
                                    val newPayments = newParticipants[editingParticipantIndex].payments.toMutableList()
                                    newPayments[editingPaymentIndex] = payment.copy(description = newDescription, amount = newAmount, excludedParticipants = excludedParticipants)
                                    newParticipants[editingParticipantIndex] = newParticipants[editingParticipantIndex].copy(payments = newPayments)
                                    participants = newParticipants
                                    showEditPaymentsDialog = false
                                    editingParticipantIndex = -1
                                    editingPaymentIndex = -1
                                }
                            )
                        }
                    }
                    else if (showSettlementsDialog){
                        ShowSettlementsDialog(settlements) {
                            showSettlementsDialog = false
                        }
                    }
                }
            }
        }
    )
}

fun calculateSettlements(participants: List<ParticipantWithPayments>): List<Settlement> {
    val balances = participants.associate { it.participant.name to 0.0 }.toMutableMap()

    participants.forEach { participantWithPayments ->
        participantWithPayments.payments.forEach { payment ->
            val includedParticipants = participants.map { it.participant.name }.subtract(payment.excludedParticipants).toList()
            val splitAmount = payment.amount / includedParticipants.size

            includedParticipants.forEach { participant ->
                balances[participant] = balances[participant]!! - splitAmount
            }
            balances[participantWithPayments.participant.name] = balances[participantWithPayments.participant.name]!! + payment.amount
        }
    }

    val totalPayments = balances.values.sum()
    val averagePayment = totalPayments / participants.size

    balances.forEach { (name, balance) ->
        balances[name] = balance - averagePayment
    }

    val debtors = balances.filter { it.value < 0 }.toMutableMap()
    val creditors = balances.filter { it.value > 0 }.toMutableMap()
    val settlements = mutableListOf<Settlement>()

    while (creditors.isNotEmpty() && debtors.isNotEmpty()) {
        val creditorEntry = creditors.entries.first()
        val debtorEntry = debtors.entries.first()

        val amountToSettle = minOf(creditorEntry.value, -debtorEntry.value)

        if (amountToSettle > 0) {
            val amountToSettleRounded = String.format(Locale.US, "%.2f", amountToSettle).toDouble()
            settlements.add(Settlement(from = debtorEntry.key, to = creditorEntry.key, amount = amountToSettleRounded))

            creditors[creditorEntry.key] = creditorEntry.value - amountToSettle
            debtors[debtorEntry.key] = debtorEntry.value + amountToSettle

            if (creditors[creditorEntry.key] == 0.0) {
                creditors.remove(creditorEntry.key)
            }
            if (debtors[debtorEntry.key] == 0.0) {
                debtors.remove(debtorEntry.key)
            }
        } else {
            creditors.remove(creditorEntry.key)
            debtors.remove(debtorEntry.key)
        }
    }

    return settlements.filter { it.amount > 0 }
}