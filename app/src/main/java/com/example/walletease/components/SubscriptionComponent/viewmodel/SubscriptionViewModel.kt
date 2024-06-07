package com.example.walletease.components.SubscriptionComponent.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.walletease.components.SubscriptionComponent.dataclass.Subscription
import com.example.walletease.fetchAndSaveFcmToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SubscriptionViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private var currentUser: FirebaseUser? = null
    private val _subscriptions = MutableLiveData<List<Subscription>>()
    val subscriptions: LiveData<List<Subscription>> = _subscriptions

    init {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            currentUser = auth.currentUser
            if (currentUser != null) {
                fetchSubscriptions()
            } else {
                _subscriptions.value = emptyList()
            }
        }
    }

    private fun fetchSubscriptions() {
        currentUser?.let { user ->
            db.collection("subscriptions")
                .whereEqualTo("userId", user.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("SubscriptionViewModel", "Listen failed.", error)
                        return@addSnapshotListener
                    }
                    val subscriptionList = ArrayList<Subscription>()
                    snapshot?.documents?.forEach { document ->
                        document.toObject(Subscription::class.java)?.let {
                            subscriptionList.add(it.copy(id = document.id))
                        }
                    }
                    _subscriptions.value = subscriptionList
                }
        }
    }

    fun addSubscription(name: String, paymentDate: Int, price: Float) {
        val newSubscription = hashMapOf(
            "name" to name,
            "paymentDate" to paymentDate,
            "subscriptionPrice" to price,
            "userId" to currentUser?.uid
        )
        db.collection("subscriptions").add(newSubscription)
            .addOnSuccessListener { documentReference ->
                Log.d("SubscriptionViewModel", "DocumentSnapshot added with ID: ${documentReference.id}")
                fetchAndSaveFcmToken()
            }
            .addOnFailureListener { e ->
                Log.w("SubscriptionViewModel", "Error adding document", e)
            }
    }

    fun updateSubscription(subscription: Subscription) {
        val updatedSubscription = hashMapOf(
            "name" to subscription.name,
            "paymentDate" to subscription.paymentDate,
            "subscriptionPrice" to subscription.subscriptionPrice,
            "userId" to currentUser?.uid
        )
        db.collection("subscriptions").document(subscription.id).set(updatedSubscription)
            .addOnSuccessListener {
                Log.d("SubscriptionViewModel", "DocumentSnapshot successfully updated!")
                fetchAndSaveFcmToken()
            }
            .addOnFailureListener { e ->
                Log.w("SubscriptionViewModel", "Error updating document", e)
            }
    }

    fun deleteSubscription(subscription: Subscription) {
        db.collection("subscriptions").document(subscription.id).delete()
    }
}
