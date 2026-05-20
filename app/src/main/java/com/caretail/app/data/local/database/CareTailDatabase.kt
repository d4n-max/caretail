package com.caretail.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.caretail.app.data.local.dao.HealthDiaryDao
import com.caretail.app.data.local.dao.PetDao
import com.caretail.app.data.local.dao.PetDocumentDao
import com.caretail.app.data.local.dao.ReminderDao
import com.caretail.app.data.local.entities.HealthDiaryEntryEntity
import com.caretail.app.data.local.entities.PetDocumentEntity
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.local.entities.ReminderEntity

@Database(
    entities = [
        PetEntity::class,
        ReminderEntity::class,
        HealthDiaryEntryEntity::class,
        PetDocumentEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class CareTailDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
    abstract fun reminderDao(): ReminderDao
    abstract fun healthDiaryDao(): HealthDiaryDao
    abstract fun petDocumentDao(): PetDocumentDao
}
