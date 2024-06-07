package com.example.walletease.components.UserConfigurationComponent.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.walletease.fetchAndSaveFcmToken
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private var _currentUser = MutableLiveData<FirebaseUser?>(null)
    val currentUser: LiveData<FirebaseUser?> get() = _currentUser

    var showError = MutableLiveData("")

    init {
        val user = Firebase.auth.currentUser
        _currentUser.value = user
    }

    fun setUser(user: FirebaseUser?) {
        _currentUser.value = user
    }

    fun setError(error: String) {
        showError.value = error
    }

    fun clearError() {
        showError.value = ""
    }

    fun login(email: String, password: String): Task<AuthResult> {
        return FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                setUser(FirebaseAuth.getInstance().currentUser)
                fetchAndSaveFcmToken()
                showError.value = ""
            } else {
                when (task.exception) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        showError.value = "Email or Password is incorrect."
                    }
                    else -> {
                        showError.value = "Failed to log in."
                    }
                }
            }
        }
    }

    fun signUp(email: String, password: String): Task<AuthResult> {
        return FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                setUser(FirebaseAuth.getInstance().currentUser)
                fetchAndSaveFcmToken()
                showError.value = ""
            } else {
                when (task.exception) {
                    is FirebaseAuthUserCollisionException -> {
                        showError.value = "User with this email already exists."
                    }
                    is FirebaseAuthWeakPasswordException -> {
                        showError.value = "The password is too weak."
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        showError.value = "The email address is malformed."
                    }
                    else -> {
                        showError.value = "Failed to create account."
                    }
                }
            }
        }
    }

    fun updatePassword(newPassword: String, callback: (Boolean, String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            it.updatePassword(newPassword).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Password updated successfully!")
                } else {
                    callback(false, "Password update failed.")
                }
            }
        } ?: callback(false, "No user is logged in.")
    }

    fun logout() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val db = FirebaseFirestore.getInstance()
            db.collection("subscriptions")
                .whereEqualTo("userId", it.uid)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        db.collection("subscriptions").document(document.id)
                            .update("fcmToken", FieldValue.delete())
                            .addOnSuccessListener { Log.d("AuthViewModel", "Token removed successfully from subscription: ${document.id}") }
                            .addOnFailureListener { e -> Log.w("AuthViewModel", "Error removing token from subscription: ${document.id}", e) }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("AuthViewModel", "Error fetching subscriptions", e)
                }
        }

        FirebaseAuth.getInstance().signOut()
        setUser(null)
    }
}