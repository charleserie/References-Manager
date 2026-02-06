package com.charleserie.referencesmanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.charleserie.referencesmanager.databinding.ItemReferenceBinding
import com.charleserie.referencesmanager.models.Reference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReferenceAdapter(
    private val onItemClick: (Reference) -> Unit,
    private val onDeleteClick: (Reference) -> Unit
) : ListAdapter<Reference, ReferenceAdapter.ReferenceViewHolder>(ReferenceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReferenceViewHolder {
        val binding = ItemReferenceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReferenceViewHolder(binding, onItemClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: ReferenceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ReferenceViewHolder(
        private val binding: ItemReferenceBinding,
        private val onItemClick: (Reference) -> Unit,
        private val onDeleteClick: (Reference) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reference: Reference) {
            binding.referenceTitle.text = reference.title.ifEmpty { "Untitled" }
            binding.referenceUrl.text = reference.url
            binding.referenceDate.text = formatDate(reference.createdAt)

            binding.root.setOnClickListener { onItemClick(reference) }
            binding.deleteButton.setOnClickListener { onDeleteClick(reference) }
        }

        private fun formatDate(timestamp: Long): String {
            val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            return formatter.format(Date(timestamp))
        }
    }

    class ReferenceDiffCallback : DiffUtil.ItemCallback<Reference>() {
        override fun areItemsTheSame(oldItem: Reference, newItem: Reference): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Reference, newItem: Reference): Boolean {
            return oldItem == newItem
        }
    }
}
