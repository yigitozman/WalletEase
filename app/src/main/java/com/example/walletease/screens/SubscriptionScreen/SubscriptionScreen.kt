package com.example.walletease.screens.SubscriptionScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.walletease.screens.SubscriptionScreen.dataclass.Subscription
import com.example.walletease.screens.SubscriptionScreen.dialogscreen.SubscriptionDialog
import com.example.walletease.screens.SubscriptionScreen.viewmodel.SubscriptionViewModel
import com.example.walletease.screens.UserConfiguration.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    subscriptionViewModel: SubscriptionViewModel
) {
    val subscriptions by subscriptionViewModel.subscriptions.observeAsState(initial = listOf())
    var isDialogOpen by remember { mutableStateOf(false) }
    var editingSubscription by remember { mutableStateOf<Subscription?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscriptions") },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingSubscription = null
                isDialogOpen = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Subscription")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(subscriptions) { subscription ->
                SubscriptionItem(
                    subscription = subscription,
                    onEdit = { sub ->
                        editingSubscription = sub
                        isDialogOpen = true
                    },
                    onDelete = { sub ->
                        subscriptionViewModel.deleteSubscription(sub)
                    }
                )
            }
        }

        if (isDialogOpen) {
            SubscriptionDialog(
                onAddOrUpdateSubscription = { name, paymentDate, price ->
                    if (editingSubscription == null) {
                        subscriptionViewModel.addSubscription(name, paymentDate, price)
                    } else {
                        subscriptionViewModel.updateSubscription(editingSubscription!!.copy(name = name, paymentDate = paymentDate, subscriptionPrice = price))
                    }
                },
                subscription = editingSubscription,
                onDismiss = { isDialogOpen = false }
            )
        }
    }
}

@Composable
fun SubscriptionItem(subscription: Subscription, onEdit: (Subscription) -> Unit, onDelete: (Subscription) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(subscription) },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subscription.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Payment Date: ${subscription.paymentDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Price: $${subscription.subscriptionPrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                IconButton(onClick = { onEdit(subscription) }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { onDelete(subscription) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}