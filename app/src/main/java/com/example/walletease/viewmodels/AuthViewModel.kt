package com.example.walletease.viewmodels

import androidx.lifecycle.ViewModel
import com.example.walletease.database.UserDao
import com.example.walletease.dataclasses.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class AuthViewModel(private val userDao: UserDao) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun setUser(user: User) {
        _user.value = user
    }

    suspend fun login(username: String, password: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.login(username, password)
        }
    }

    suspend fun isUserExists(username: String, password: String): Boolean {
        return userDao.findUser(username, password) != null
    }

    suspend fun signUp(user: User) {
        withContext(Dispatchers.IO) {
            userDao.signUp(user)
        }
    }
}