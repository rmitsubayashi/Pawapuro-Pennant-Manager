package com.rmitsubayashi.pennantmanager.ui.notelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.rmitsubayashi.pennantmanager.data.model.Note
import com.rmitsubayashi.pennantmanager.databinding.ItemNoteListBinding

class NoteListAdapter(
    lifecycleOwner: LifecycleOwner,
    private val viewModel: NoteListViewModel,
    private val contextMenuListener: View.OnCreateContextMenuListener
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list = mutableListOf<Note>()

    override fun getItemCount(): Int = list.size

    init {
        viewModel.notes.observe(lifecycleOwner) {
            list.clear()
            list.addAll(it)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemNoteListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteListViewHolder(viewModel, binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val note = list[position]
        (holder as NoteListViewHolder).setNote(note, contextMenuListener, position)
    }

}