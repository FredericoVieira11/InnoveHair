package com.example.innovehair.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_credentials")
data class UserCredentials(
    @PrimaryKey val userId: String,
    val email: String,
    val password: String
)