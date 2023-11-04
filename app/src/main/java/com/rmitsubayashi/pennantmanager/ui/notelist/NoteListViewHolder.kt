package com.rmitsubayashi.pennantmanager.ui.notelist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.rmitsubayashi.pennantmanager.data.model.Note
import com.rmitsubayashi.pennantmanager.databinding.ItemNoteListBinding

class NoteListViewHolder(private val viewModel: NoteListViewModel, private val binding: ItemNoteListBinding) : RecyclerView.ViewHolder(binding.root) {
    fun setNote(note: Note, contextMenuListener: View.OnCreateContextMenuListener) {
        binding.titleTextview.text = note.title

        binding.root.setOnClickListener {
            viewModel.viewNote(note)
        }
        binding.root.setOnCreateContextMenuListener(contextMenuListener)
        binding.root.setOnLongClickListener {
            viewModel.longClickListItem(note)
            // we want to continue events (opening context menu)
            false
        }
    }
}