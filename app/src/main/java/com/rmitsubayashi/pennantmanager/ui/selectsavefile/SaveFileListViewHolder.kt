package com.rmitsubayashi.pennantmanager.ui.selectsavefile

import androidx.recyclerview.widget.RecyclerView
import com.rmitsubayashi.pennantmanager.data.model.SaveFile
import com.rmitsubayashi.pennantmanager.databinding.ItemSaveFileListBinding

class SaveFileListViewHolder(private val viewModel: SelectSaveFileViewModel, private val binding: ItemSaveFileListBinding) : RecyclerView.ViewHolder(binding.root) {
    fun setSaveFile(saveFile: SaveFile, isSelected: Boolean) {
        binding.nameTextview.text = saveFile.name
        binding.root.setOnClickListener {
            viewModel.selectSaveFile(saveFile)
        }
        binding.checkbox.isChecked = isSelected
    }
}