package com.rmitsubayashi.pennantmanager.ui.playerlist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.rmitsubayashi.pennantmanager.R
import com.rmitsubayashi.pennantmanager.data.model.Player
import com.rmitsubayashi.pennantmanager.databinding.ItemPlayerListBinding
import com.rmitsubayashi.pennantmanager.ui.util.PositionStringMapper

class PlayerListViewHolder(private val viewModel: PlayerListViewModel, private val binding: ItemPlayerListBinding) : RecyclerView.ViewHolder(binding.root) {
    fun setPlayer(player: Player, contextMenuListener: View.OnCreateContextMenuListener) {
        binding.nameTextview.text = player.name
        binding.ageTextview.text = binding.root.context.getString(R.string.age, player.age)
        binding.positionsTextview.text = player.positions.joinToString("") {
            val resId = PositionStringMapper.mapPositionToAbbreviationStringRes(it)
            binding.root.context.getString(resId)
        }

        binding.root.setOnCreateContextMenuListener(contextMenuListener)
        binding.root.setOnLongClickListener {
            viewModel.longClickListItem(player)
            // we want to continue events (opening context menu)
            false
        }
    }
}