package com.example.bloom.model.model.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bloom.model.model.dao.DiscoveryDao
import com.example.bloom.model.model.dao.UserDao
import com.example.bloom.model.model.entities.Discovery
import com.example.bloom.model.model.entities.User

@Database(entities = [User::class, Discovery::class], version = 1, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun discoveryDao(): DiscoveryDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bloom_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
