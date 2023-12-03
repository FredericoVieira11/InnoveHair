package com.example.innovehair.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserCredentials::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userCredentialsDao(): UserCredentialsDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                return instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build().also {
                    instance = it
                }
            }
        }
    }
}