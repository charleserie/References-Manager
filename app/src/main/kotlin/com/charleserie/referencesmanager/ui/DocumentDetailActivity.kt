package com.charleserie.referencesmanager.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.charleserie.referencesmanager.R
import com.charleserie.referencesmanager.adapters.ReferenceAdapter
import com.charleserie.referencesmanager.databinding.ActivityDocumentDetailBinding
import com.charleserie.referencesmanager.viewmodels.ReferenceViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class DocumentDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentDetailBinding
    private lateinit var referenceViewModel: ReferenceViewModel
    private lateinit var referenceAdapter: ReferenceAdapter
    private var documentId: Long = 0L
    private var documentName: String = ""
    private var sharedUrl: String? = null
    private var shouldAddToDocument: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        documentId = intent.getLongExtra("documentId", 0L)
        documentName = intent.getStringExtra("documentName") ?: "Document"
        sharedUrl = intent.getStringExtra("sharedUrl")
        shouldAddToDocument = intent.getBooleanExtra("addToDocument", false)

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupListeners()

        binding.documentTitle.text = documentName

        if (shouldAddToDocument && !sharedUrl.isNullOrEmpty()) {
            addReferenceFromShare(sharedUrl!!)
        }
    }

    private fun setupViewModel() {
        val savedStateHandle = SavedStateHandle().apply {
            set("documentId", documentId)
        }

        referenceViewModel = ReferenceViewModel(application, savedStateHandle)
    }

    private fun setupRecyclerView() {
        referenceAdapter = ReferenceAdapter(
            onItemClick = { reference ->
                openUrl(reference.url)
            },
            onDeleteClick = { reference ->
                showDeleteConfirmation(reference)
            }
        )

        binding.referencesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DocumentDetailActivity)
            adapter = referenceAdapter
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            referenceViewModel.references.collect { references ->
                referenceAdapter.submitList(references)
                binding.emptyStateText.visibility =
                    if (references.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            }
        }
    }

    private fun setupListeners() {
        binding.fabAddReference.setOnClickListener {
            showAddReferenceDialog()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun showAddReferenceDialog() {
        val urlEditText = TextInputEditText(this).apply {
            hint = getString(R.string.url_hint)
            setPadding(16, 16, 16, 16)
        }

        val titleEditText = TextInputEditText(this).apply {
            hint = getString(R.string.title_hint)
            setPadding(16, 16, 16, 16)
        }

        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            )
            addView(urlEditText)
            addView(titleEditText)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.add_reference)
            .setView(container)
            .setPositiveButton(R.string.add) { _, _ ->
                val url = urlEditText.text.toString().trim()
                val title = titleEditText.text.toString().trim()

                if (url.isNotEmpty()) {
                    referenceViewModel.addReference(url, title)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun addReferenceFromShare(url: String) {
        val titleEditText = TextInputEditText(this).apply {
            hint = getString(R.string.title_hint)
            setPadding(16, 16, 16, 16)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.add_reference)
            .setMessage(url)
            .setView(titleEditText)
            .setPositiveButton(R.string.add) { _, _ ->
                val title = titleEditText.text.toString().trim()
                referenceViewModel.addReference(url, title)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                finish()
            }
            .setOnCancelListener {
                finish()
            }
            .show()
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            android.widget.Toast.makeText(
                this,
                getString(R.string.error_opening_url),
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showDeleteConfirmation(reference: com.charleserie.referencesmanager.models.Reference) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_reference)
            .setMessage(R.string.delete_reference_confirm)
            .setPositiveButton(R.string.delete) { _, _ ->
                referenceViewModel.deleteReference(reference)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
