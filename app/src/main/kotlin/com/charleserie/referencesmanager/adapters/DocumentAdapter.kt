package com.charleserie.referencesmanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.charleserie.referencesmanager.databinding.ItemDocumentBinding
import com.charleserie.referencesmanager.models.Document
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DocumentAdapter(
    private val onItemClick: (Document) -> Unit,
    private val onDeleteClick: (Document) -> Unit
) : ListAdapter<Document, DocumentAdapter.DocumentViewHolder>(DocumentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val binding = ItemDocumentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DocumentViewHolder(binding, onItemClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DocumentViewHolder(
        private val binding: ItemDocumentBinding,
        private val onItemClick: (Document) -> Unit,
        private val onDeleteClick: (Document) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(document: Document) {
            binding.documentName.text = document.name
            binding.documentDate.text = formatDate(document.createdAt)

            binding.root.setOnClickListener { onItemClick(document) }
            binding.deleteButton.setOnClickListener { onDeleteClick(document) }
        }

        private fun formatDate(timestamp: Long): String {
            val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            return formatter.format(Date(timestamp))
        }
    }

    class DocumentDiffCallback : DiffUtil.ItemCallback<Document>() {
        override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
            return oldItem == newItem
        }
    }
}
