package com.example.walletease.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.walletease.dataclasses.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    fun login(username: String, password: String): User?

    @Query("SELECT * FROM users WHERE username = :username OR password = :password")
    suspend fun findUser(username: String, password: String): User?

    @Insert
    fun signUp(user: User)
}