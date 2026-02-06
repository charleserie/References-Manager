package com.charleserie.referencesmanager.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.charleserie.referencesmanager.R
import com.charleserie.referencesmanager.adapters.DocumentAdapter
import com.charleserie.referencesmanager.databinding.ActivityShareReceiveBinding
import com.charleserie.referencesmanager.viewmodels.DocumentViewModel
import kotlinx.coroutines.launch

class ShareReceiveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShareReceiveBinding
    private val documentViewModel: DocumentViewModel by viewModels()
    private lateinit var documentAdapter: DocumentAdapter
    private var sharedUrl: String = ""
    private var sharedText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareReceiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        extractSharedContent()
        setupRecyclerView()
        setupObservers()
    }

    private fun extractSharedContent() {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                val type = intent?.type
                if (type == "text/plain") {
                    sharedText = intent?.getStringExtra(Intent.EXTRA_TEXT) ?: ""
                    sharedUrl = extractUrl(sharedText)
                }
            }
            Intent.ACTION_VIEW -> {
                sharedUrl = intent?.dataString ?: ""
            }
        }

        binding.sharedUrlText.text = sharedUrl.ifEmpty { getString(R.string.no_url_found) }
    }

    private fun extractUrl(text: String): String {
        val urlPattern = """https?://[^\s]+""".toRegex()
        return urlPattern.find(text)?.value ?: text
    }

    private fun setupRecyclerView() {
        documentAdapter = DocumentAdapter(
            onItemClick = { document ->
                if (sharedUrl.isNotEmpty()) {
                    navigateToDocumentWithUrl(document.id, sharedUrl)
                }
            },
            onDeleteClick = { }
        )

        binding.documentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ShareReceiveActivity)
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

    private fun navigateToDocumentWithUrl(documentId: Long, url: String) {
        val intent = Intent(this, DocumentDetailActivity::class.java).apply {
            putExtra("documentId", documentId)
            putExtra("sharedUrl", url)
            putExtra("addToDocument", true)
        }
        startActivity(intent)
        finish()
    }
}
