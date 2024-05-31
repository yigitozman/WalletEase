package com.example.walletease.screens.DashboardScreen.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.walletease.screens.DashboardScreen.dataclasses.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class TransactionViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private var currentUser: FirebaseUser? = null

    private val _incomeTransactions = MutableLiveData<List<Transaction>>()
    val incomeTransactions: LiveData<List<Transaction>> = _incomeTransactions

    private val _expenseTransactions = MutableLiveData<List<Transaction>>()
    val expenseTransactions: LiveData<List<Transaction>> = _expenseTransactions

    init {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            currentUser = auth.currentUser
            if (currentUser != null) {
                fetchTransactions("income")
                fetchTransactions("expense")
            } else {
                _incomeTransactions.value = emptyList()
                _expenseTransactions.value = emptyList()
            }
        }
    }

    private fun fetchTransactions(type: String) {
        currentUser?.let { user ->
            db.collection(type)
                .whereEqualTo("userId", user.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("TransactionViewModel", "Listen failed.", error)
                        return@addSnapshotListener
                    }
                    val transactionList = ArrayList<Transaction>()
                    snapshot?.documents?.forEach { document ->
                        document.toObject(Transaction::class.java)?.let {
                            transactionList.add(it.copy(id = document.id))
                        }
                    }
                    when (type) {
                        "income" -> _incomeTransactions.value = transactionList
                        "expense" -> _expenseTransactions.value = transactionList
                    }
                }
        }
    }

    fun addTransaction(type: String, transaction: Transaction) {
        currentUser?.let { user ->
            val newTransaction = hashMapOf(
                "name" to transaction.name,
                "amount" to transaction.amount,
                "date" to transaction.date,
                "userId" to user.uid
            )
            db.collection(type).add(newTransaction)
                .addOnSuccessListener { documentReference ->
                    Log.d("TransactionViewModel", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("TransactionViewModel", "Error adding document", e)
                }
        }
    }

    fun updateTransaction(type: String, transaction: Transaction) {
        db.collection(type).document(transaction.id).set(transaction)
            .addOnSuccessListener {
                Log.d("TransactionViewModel", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("TransactionViewModel", "Error updating document", e)
            }
    }

    fun deleteTransaction(type: String, transaction: Transaction) {
        db.collection(type).document(transaction.id).delete()
    }
}