package com.rmitsubayashi.pennantmanager.ui.selectsavefile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.rmitsubayashi.pennantmanager.data.model.SaveFile
import com.rmitsubayashi.pennantmanager.databinding.ItemSaveFileListBinding

class SaveFileListAdapter(lifecycleOwner: LifecycleOwner, private val viewModel: SelectSaveFileViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list = mutableListOf<SaveFile>()
    private var selectedSaveFile: SaveFile? = null

    override fun getItemCount(): Int = list.size

    init {
        viewModel.saveFiles.observe(lifecycleOwner) {
            list.clear()
            list.addAll(it)
            notifyDataSetChanged()
        }

        viewModel.saveFileSelectedOnScreen.observe(lifecycleOwner) {
            selectedSaveFile = it
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemSaveFileListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SaveFileListViewHolder(viewModel, binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val saveFile = list[position]
        (holder as SaveFileListViewHolder).setSaveFile(saveFile, saveFile.id == selectedSaveFile?.id)
    }
}