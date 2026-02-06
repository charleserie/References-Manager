package com.charleserie.referencesmanager.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.charleserie.referencesmanager.models.Reference
import kotlinx.coroutines.flow.Flow

@Dao
interface ReferenceDao {
    @Query("SELECT * FROM references WHERE documentId = :documentId ORDER BY createdAt DESC")
    fun getReferencesByDocument(documentId: Long): Flow<List<Reference>>

    @Query("SELECT * FROM references WHERE id = :referenceId")
    suspend fun getReferenceById(referenceId: Long): Reference?

    @Insert
    suspend fun insertReference(reference: Reference): Long

    @Update
    suspend fun updateReference(reference: Reference)

    @Delete
    suspend fun deleteReference(reference: Reference)

    @Query("DELETE FROM references WHERE id = :referenceId")
    suspend fun deleteReferenceById(referenceId: Long)

    @Query("SELECT COUNT(*) FROM references WHERE documentId = :documentId")
    suspend fun getReferenceCount(documentId: Long): Int
}
