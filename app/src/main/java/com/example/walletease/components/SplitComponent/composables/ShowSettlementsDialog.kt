package com.example.walletease.components.SplitComponent.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.walletease.components.SplitComponent.dataclasses.Settlement

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
                if(settlements.isEmpty()) {
                    Text("Everything is settled.")
                } else {
                    settlements.forEach { settlement ->
                        Text("${settlement.from} should pay ${settlement.to} ${settlement.amount}")
                    }
                }
            }
        }
    )
}