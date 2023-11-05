package com.rmitsubayashi.pennantmanager.ui.notelist

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rmitsubayashi.pennantmanager.R
import com.rmitsubayashi.pennantmanager.data.model.Note
import com.rmitsubayashi.pennantmanager.databinding.ItemNoteListBinding

class NoteListViewHolder(private val viewModel: NoteListViewModel, private val binding: ItemNoteListBinding) : RecyclerView.ViewHolder(binding.root) {
    fun setNote(note: Note, contextMenuListener: View.OnCreateContextMenuListener, position: Int) {
        binding.titleTextview.text = note.title

        val lineColor = if (position % 2 == 0) {
            ContextCompat.getColor(binding.root.context, R.color.purple_200)
        } else {
            ContextCompat.getColor(binding.root.context, R.color.purple_700)
        }
        binding.line.setBackgroundColor(lineColor)

        binding.root.setOnClickListener {
            viewModel.viewNote(note)
        }
        binding.root.setOnCreateContextMenuListener(contextMenuListener)
        binding.root.setOnLongClickListener {
            if (!note.isSaveFileNote()) {
                viewModel.longClickListItem(note)
                // we want to continue events (opening context menu)
                false
            } else true

        }
    }
}