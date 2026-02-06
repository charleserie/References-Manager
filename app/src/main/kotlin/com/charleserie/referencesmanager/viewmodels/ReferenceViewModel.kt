package com.charleserie.referencesmanager.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.charleserie.referencesmanager.database.ReferencesDatabase
import com.charleserie.referencesmanager.models.Reference
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReferenceViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val database = ReferencesDatabase.getInstance(application)
    private val referenceDao = database.referenceDao()

    private val documentId: Long = savedStateHandle.get<Long>("documentId") ?: 0L

    val references = referenceDao.getReferencesByDocument(documentId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addReference(url: String, title: String = "", description: String = "") {
        viewModelScope.launch {
            val reference = Reference(
                documentId = documentId,
                url = url,
                title = title,
                description = description
            )
            referenceDao.insertReference(reference)
        }
    }

    fun deleteReference(reference: Reference) {
        viewModelScope.launch {
            referenceDao.deleteReference(reference)
        }
    }

    fun updateReference(reference: Reference) {
        viewModelScope.launch {
            referenceDao.updateReference(reference)
        }
    }
}
