package com.charleserie.referencesmanager.models

import androidx.room.Relation

data class DocumentWithReferences(
    @androidx.room.Embedded
    val document: Document,
    @Relation(
        parentColumn = "id",
        entityColumn = "documentId"
    )
    val references: List<Reference>
)
