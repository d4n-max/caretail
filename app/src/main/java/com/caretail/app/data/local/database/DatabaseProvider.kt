package com.caretail.app.data.local.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private const val DATABASE_NAME = "caretail.db"

    @Volatile
    private var instance: CareTailDatabase? = null

    fun getDatabase(context: Context): CareTailDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                CareTailDatabase::class.java,
                DATABASE_NAME,
            ).build().also { database ->
                instance = database
            }
        }
    }
}
