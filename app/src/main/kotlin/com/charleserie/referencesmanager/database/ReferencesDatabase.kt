package com.charleserie.referencesmanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.charleserie.referencesmanager.models.Document
import com.charleserie.referencesmanager.models.Reference

@Database(
    entities = [Document::class, Reference::class],
    version = 1,
    exportSchema = false
)
abstract class ReferencesDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun referenceDao(): ReferenceDao

    companion object {
        @Volatile
        private var instance: ReferencesDatabase? = null

        fun getInstance(context: Context): ReferencesDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ReferencesDatabase::class.java,
                    "references_database"
                ).build().also { instance = it }
            }
        }
    }
}
