package com.charleserie.referencesmanager.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.charleserie.referencesmanager.R
import com.charleserie.referencesmanager.adapters.DocumentAdapter
import com.charleserie.referencesmanager.databinding.ActivityMainBinding
import com.charleserie.referencesmanager.models.Document
import com.charleserie.referencesmanager.viewmodels.DocumentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val documentViewModel: DocumentViewModel by viewModels()
    private lateinit var documentAdapter: DocumentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        documentAdapter = DocumentAdapter(
            onItemClick = { document ->
                val intent = Intent(this, DocumentDetailActivity::class.java).apply {
                    putExtra("documentId", document.id)
                    putExtra("documentName", document.name)
                }
                startActivity(intent)
            },
            onDeleteClick = { document ->
                showDeleteConfirmation(document)
            }
        )

        binding.documentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = documentAdapter
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            documentViewModel.documents.collect { documents ->
                documentAdapter.submitList(documents)
                binding.emptyStateText.visibility =
                    if (documents.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            }
        }
    }

    private fun setupListeners() {
        binding.fabCreateDocument.setOnClickListener {
            showCreateDocumentDialog()
        }
    }

    private fun showCreateDocumentDialog() {
        val editText = TextInputEditText(this).apply {
            hint = getString(R.string.document_name_hint)
            setPadding(16, 16, 16, 16)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.create_document)
            .setView(editText)
            .setPositiveButton(R.string.create) { _, _ ->
                val documentName = editText.text.toString().trim()
                if (documentName.isNotEmpty()) {
                    documentViewModel.createDocument(documentName)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showDeleteConfirmation(document: Document) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_document)
            .setMessage(getString(R.string.delete_document_confirm, document.name))
            .setPositiveButton(R.string.delete) { _, _ ->
                documentViewModel.deleteDocument(document)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
