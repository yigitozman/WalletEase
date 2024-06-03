package com.example.walletease.components.SplitComponent.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.walletease.components.SplitComponent.dataclasses.ParticipantWithPayments

@Composable
fun ParticipantsInput(
    participants: List<ParticipantWithPayments>,
    onParticipantsChange: (List<ParticipantWithPayments>) -> Unit,
    onEditPayment: (Int, Int) -> Unit
) {
    Column {
        participants.forEachIndexed { participantIndex, participantWithPayments ->
            var text by rememberSaveable { mutableStateOf("") }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { newName ->
                            text = newName
                            val newParticipants = participants.toMutableList()
                            newParticipants[participantIndex] = participantWithPayments.copy(participant = participantWithPayments.participant.copy(name = newName))
                            onParticipantsChange(newParticipants)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        placeholder = { Text(participantWithPayments.participant.name) },
                        label = { Text("Participant Name") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    PaymentsInput(
                        payments = participantWithPayments.payments,
                        onPaymentsChange = { newPayments ->
                            val newParticipants = participants.toMutableList()
                            newParticipants[participantIndex] = participantWithPayments.copy(payments = newPayments)
                            onParticipantsChange(newParticipants)
                        },
                        onEditPayment = { paymentIndex -> onEditPayment(participantIndex, paymentIndex) }
                    )
                }
            }
        }
    }
}