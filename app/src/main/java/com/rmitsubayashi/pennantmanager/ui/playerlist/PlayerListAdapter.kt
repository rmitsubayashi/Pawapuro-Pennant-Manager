package com.rmitsubayashi.pennantmanager.ui.playerlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.rmitsubayashi.pennantmanager.data.model.Player
import com.rmitsubayashi.pennantmanager.databinding.ItemPlayerListBinding

class PlayerListAdapter(lifecycleOwner: LifecycleOwner, private val viewModel: PlayerListViewModel, private val contextMenuListener: View.OnCreateContextMenuListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list = mutableListOf<Player>()

    override fun getItemCount(): Int = list.size

    init {
        viewModel.players.observe(lifecycleOwner, Observer {
            list.clear()
            list.addAll(it)
            notifyDataSetChanged()
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemPlayerListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerListViewHolder(viewModel, binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val player = list[position]
        (holder as PlayerListViewHolder).setPlayer(player, contextMenuListener)
    }
}