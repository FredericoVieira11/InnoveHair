package com.example.innovehair.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserCredentialsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserCredentials(userCredentials: UserCredentials)

    //@Query("SELECT * FROM user_credentials WHERE userId = :userId")
    //suspend fun getUserCredentialsByEmail(email: String): UserCredentials?

    @Query("DELETE FROM user_credentials")
    suspend fun deleteAllUserCredentials()
}