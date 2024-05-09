package com.example.walletease.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser

//todo: make it check if password is used before. Also better error messages
class AuthViewModel(private val state: SavedStateHandle) : ViewModel() {

    private var _currentUser = MutableLiveData<FirebaseUser?>(null)
    val currentUser: LiveData<FirebaseUser?> get() = _currentUser

    var showError = MutableLiveData("")

    fun setUser(user: FirebaseUser?) {
        _currentUser.value = user
    }

    fun clearError() {
        showError.value = ""
    }

    fun login(email: String, password: String): Task<AuthResult> {
        return FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                setUser(FirebaseAuth.getInstance().currentUser)
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
            if (!task.isSuccessful) {
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
}