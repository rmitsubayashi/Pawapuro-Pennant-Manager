package com.rmitsubayashi.pennantmanager.ui.playerlist

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rmitsubayashi.pennantmanager.R
import com.rmitsubayashi.pennantmanager.databinding.FragmentPlayerListBinding
import com.rmitsubayashi.pennantmanager.ui.addeditplayer.AddEditPlayerFragment
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class PlayerListFragment : Fragment() {

    private val viewModel: PlayerListViewModel by viewModels()
    private var _binding: FragmentPlayerListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addToolbarMenu()
        viewModel.fetchPlayerList()
        bindListAdapter()
        addObservers()
        addListeners()
    }

    private fun addToolbarMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.player_list_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_filter -> {
                        viewModel.openFilter()
                        true
                    }
                    R.id.menu_edit_current_date -> {
                        viewModel.editCurrentDate()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

        }, viewLifecycleOwner)
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            ID_EDIT -> {
                viewModel.editPlayer()
            }
            ID_REMOVE -> {
                viewModel.removePlayer()
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun getContextMenuListener(): View.OnCreateContextMenuListener =
        View.OnCreateContextMenuListener {
            contextMenu, _, _ ->
            contextMenu.add(Menu.NONE, ID_EDIT, Menu.NONE, requireContext().getString(R.string.edit_player_list_item))
            contextMenu.add(Menu.NONE, ID_REMOVE, Menu.NONE, requireContext().getString(R.string.remove_player_list_item))
        }

    private fun bindListAdapter() {
        binding.playerList.adapter = PlayerListAdapter(viewLifecycleOwner, viewModel, getContextMenuListener())
    }

    private fun addObservers() {
        viewModel.lastRemovedPlayer.observe(viewLifecycleOwner) {
            if (it.hasBeenHandled) return@observe
            Snackbar.make(binding.root, R.string.undo_remove, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo_remove_confirm) {
                    viewModel.undoRemove()
                }
                .show()
        }

        viewModel.addEditEvent.observe(viewLifecycleOwner) {
            if (it.hasBeenHandled) return@observe
            val playerID = it.getContentIfNotHandled()?.id ?: AddEditPlayerFragment.ARG_NEW_PLAYER_ID
            val navAction = PlayerListFragmentDirections.actionPlayerListFragmentToAddEditPlayerFragment(playerID)
            binding.root.findNavController().navigate(navAction)
        }

        viewModel.editCurrentDateEvent.observe(viewLifecycleOwner) {
            val date = it.getContentIfNotHandled() ?: return@observe
            DatePickerDialog(
                requireContext(),
                {
                    _, year, month, day ->
                    viewModel.updateCurrentDate(LocalDate.of(year, month+1, day))
                },
                date.year, date.monthValue-1, date.dayOfMonth).show()
        }

        viewModel.openFilterEvent.observe(viewLifecycleOwner) {
            val filterState = it.getContentIfNotHandled() ?: return@observe
            val filterBottomSheet = PlayerListFilterBottomSheetFragment.newInstance(filterState, object : OnFilterSelectedListener {
                override fun onFilterSelected(filterState: FilterState) {
                    viewModel.updateFilter(filterState)
                }

            })
            filterBottomSheet.show(requireActivity().supportFragmentManager, filterBottomSheet.tag)
        }

        viewModel.currentDate.observe(viewLifecycleOwner) {
            requireActivity().findViewById<Toolbar>(R.id.toolbar).title = it
        }
    }

    private fun addListeners() {
        binding.addPlayerFab.setOnClickListener {
            viewModel.addPlayerEvent()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ID_REMOVE = 0
        private const val ID_EDIT = 1
    }

    interface OnFilterSelectedListener {
        fun onFilterSelected(filterState: FilterState)
    }
}