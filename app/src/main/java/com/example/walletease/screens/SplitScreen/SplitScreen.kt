package com.example.walletease.screens.SplitScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.walletease.screens.UserConfiguration.viewmodel.AuthViewModel

//todo: some little ui problems keyboard text box etc.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitScreen(navController: NavController, authViewModel: AuthViewModel) {
    var participantCount by rememberSaveable { mutableStateOf(0) }
    var participants by rememberSaveable { mutableStateOf(listOf<ParticipantWithPayments>()) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var settlements by rememberSaveable { mutableStateOf(listOf<Settlement>()) }
    var editingParticipantIndex by rememberSaveable { mutableStateOf(-1) }
    var editingPaymentIndex by rememberSaveable { mutableStateOf(-1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Split Screen") },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        floatingActionButton = {
            if (participants.isNotEmpty()) {
                FloatingActionButton(onClick = {
                    settlements = calculateSettlements(participants)
                    showDialog = true
                }) {
                    Icon(Icons.Default.Check, contentDescription = "Calculate Settlements")
                }
            }
        },
        content = { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .imePadding(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (participants.isEmpty()) {
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
                                enabled = false
                            )
                            IconButton(onClick = {
                                participantCount += 1
                            }) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Add Participant")
                            }
                        }
                        Button(
                            onClick = {
                                participants = List(participantCount) { ParticipantWithPayments(Participant("Participant ${it + 1}"), mutableListOf()) }
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
                            showDialog = true
                        })

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (showDialog) {
                        if (editingParticipantIndex >= 0 && editingPaymentIndex >= 0) {
                            val participant = participants[editingParticipantIndex]
                            val payment = participant.payments[editingPaymentIndex]
                            val allParticipantNames = participants.map { it.participant.name }
                            EditPaymentDialog(
                                payment = payment,
                                allParticipants = allParticipantNames,
                                onDismiss = { showDialog = false },
                                onSave = { newDescription, newAmount, excludedParticipants ->
                                    val newParticipants = participants.toMutableList()
                                    val newPayments = newParticipants[editingParticipantIndex].payments.toMutableList()
                                    newPayments[editingPaymentIndex] = payment.copy(description = newDescription, amount = newAmount, excludedParticipants = excludedParticipants)
                                    newParticipants[editingParticipantIndex] = newParticipants[editingParticipantIndex].copy(payments = newPayments)
                                    participants = newParticipants
                                    showDialog = false
                                    editingParticipantIndex = -1
                                    editingPaymentIndex = -1
                                }
                            )
                        } else {
                            ShowSettlementsDialog(settlements) {
                                showDialog = false
                            }
                        }
                    }
                }
            }
        }
    )
}

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
                        label = { Text("Participant Name") }
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

@Composable
fun PaymentsInput(
    payments: List<Payment>,
    onPaymentsChange: (List<Payment>) -> Unit,
    onEditPayment: (Int) -> Unit
) {
    var newPaymentDescription by remember { mutableStateOf("") }
    var newPaymentAmount by remember { mutableStateOf("") }

    Column {
        payments.forEachIndexed { index, payment ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = payment.description,
                    onValueChange = { newDescription ->
                        val newPayments = payments.toMutableList()
                        newPayments[index] = payment.copy(description = newDescription)
                        onPaymentsChange(newPayments)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                OutlinedTextField(
                    value = payment.amount.toString(),
                    onValueChange = { newAmount ->
                        val newPayments = payments.toMutableList()
                        newPayments[index] = payment.copy(amount = newAmount.toDoubleOrNull() ?: 0.0)
                        onPaymentsChange(newPayments)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                IconButton(onClick = { onEditPayment(index) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Payment")
                }
                IconButton(onClick = {
                    val newPayments = payments.toMutableList().also { it.removeAt(index) }
                    onPaymentsChange(newPayments)
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Payment")
                }
            }
        }

        OutlinedTextField(
            value = newPaymentDescription,
            onValueChange = { newPaymentDescription = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            placeholder = { Text("New payment description") },
            label = { Text("Description") }
        )

        OutlinedTextField(
            value = newPaymentAmount,
            onValueChange = { newPaymentAmount = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            placeholder = { Text("New payment amount") },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Button(
            onClick = {
                val newPayment = Payment(
                    description = newPaymentDescription,
                    amount = newPaymentAmount.toDoubleOrNull() ?: 0.0
                )
                onPaymentsChange(payments + newPayment)
                newPaymentDescription = ""
                newPaymentAmount = ""
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Payment")
        }
    }
}

@Composable
fun EditPaymentDialog(
    payment: Payment,
    allParticipants: List<String>,
    onDismiss: () -> Unit,
    onSave: (String, Double, List<String>) -> Unit
) {
    var newDescription by remember { mutableStateOf(payment.description) }
    var newAmount by remember { mutableStateOf(payment.amount.toString()) }
    val excludedParticipants = remember { mutableStateListOf<String>().also { it.addAll(payment.excludedParticipants) } }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edit Payment") },
        confirmButton = {
            Button(onClick = {
                onSave(newDescription, newAmount.toDoubleOrNull() ?: 0.0, excludedParticipants.toList())
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = newDescription,
                    onValueChange = { newDescription = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text("Description") }
                )
                OutlinedTextField(
                    value = newAmount,
                    onValueChange = { newAmount = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Exclude Participants:", style = MaterialTheme.typography.bodyLarge)

                allParticipants.forEach { participant ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = participant in excludedParticipants,
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    excludedParticipants.add(participant)
                                } else {
                                    excludedParticipants.remove(participant)
                                }
                            }
                        )
                        Text(participant, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    )
}

@Composable
fun ShowSettlementsDialog(settlements: List<Settlement>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("OK")
            }
        },
        text = {
            Column {
                settlements.forEach { settlement ->
                    Text("${settlement.from} should pay ${settlement.to} $${settlement.amount}")
                }
            }
        }
    )
}

data class Participant(val name: String)
data class Payment(val description: String, val amount: Double, val excludedParticipants: List<String> = listOf())
data class ParticipantWithPayments(val participant: Participant, val payments: List<Payment>)
data class Settlement(val from: String, val to: String, val amount: Double)

fun calculateSettlements(participants: List<ParticipantWithPayments>): List<Settlement> {
    val balances = participants.associate { it.participant.name to 0.0 }.toMutableMap()

    participants.forEach { participantWithPayments ->
        participantWithPayments.payments.forEach { payment ->
            if (!payment.excludedParticipants.contains(participantWithPayments.participant.name)) {
                balances[participantWithPayments.participant.name] = balances[participantWithPayments.participant.name]!! + payment.amount
            }
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
        settlements.add(Settlement(from = debtorEntry.key, to = creditorEntry.key, amount = amountToSettle))

        creditors[creditorEntry.key] = creditorEntry.value - amountToSettle
        debtors[debtorEntry.key] = debtorEntry.value + amountToSettle

        if (creditors[creditorEntry.key] == 0.0) {
            creditors.remove(creditorEntry.key)
        }
        if (debtors[debtorEntry.key] == 0.0) {
            debtors.remove(debtorEntry.key)
        }
    }

    return settlements
}