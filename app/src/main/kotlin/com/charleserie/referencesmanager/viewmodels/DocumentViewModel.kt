package com.charleserie.referencesmanager.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.charleserie.referencesmanager.database.ReferencesDatabase
import com.charleserie.referencesmanager.models.Document
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DocumentViewModel(application: Application) : AndroidViewModel(application) {
    private val database = ReferencesDatabase.getInstance(application)
    private val documentDao = database.documentDao()

    val documents = documentDao.getAllDocuments()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun createDocument(name: String) {
        viewModelScope.launch {
            val document = Document(name = name)
            documentDao.insertDocument(document)
        }
    }

    fun deleteDocument(document: Document) {
        viewModelScope.launch {
            documentDao.deleteDocument(document)
        }
    }

    fun updateDocument(document: Document) {
        viewModelScope.launch {
            documentDao.updateDocument(document.copy(updatedAt = System.currentTimeMillis()))
        }
    }
}
