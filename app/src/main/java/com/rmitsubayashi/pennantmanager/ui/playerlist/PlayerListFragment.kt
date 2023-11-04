package com.rmitsubayashi.pennantmanager.ui.playerlist

import android.os.Bundle
import android.view.*
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rmitsubayashi.pennantmanager.R
import com.rmitsubayashi.pennantmanager.data.model.Player
import com.rmitsubayashi.pennantmanager.databinding.FragmentPlayerListBinding
import com.rmitsubayashi.pennantmanager.ui.util.YearPickerUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerListFragment : Fragment() {

    private val viewModel: PlayerListViewModel by viewModels()
    private var _binding: FragmentPlayerListBinding? = null
    private val binding get() = _binding!!

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
        addViewListeners()
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
                    R.id.menu_edit_current_year -> {
                        viewModel.editCurrentYear()
                        true
                    }
                    R.id.menu_change_save_file -> {
                        val action = PlayerListFragmentDirections.actionPlayerListFragmentToSelectSaveFileFragment()
                        findNavController().navigate(action)
                        true
                    }
                    R.id.menu_open_notes -> {
                        val action = PlayerListFragmentDirections.actionPlayerListFragmentToNoteListFragment()
                        findNavController().navigate(action)
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
            val player = it.getContentIfNotHandled() ?: return@observe
            Snackbar.make(binding.root, R.string.undo_remove, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo_remove_confirm) {
                    viewModel.undoRemove(player)
                }
                .show()
        }

        viewModel.addEditEvent.observe(viewLifecycleOwner) {
            if (it.hasBeenHandled) return@observe
            val playerID = it.getContentIfNotHandled()?.id ?: Player.DEFAULT_ID
            val navAction = PlayerListFragmentDirections.actionPlayerListFragmentToAddEditPlayerFragment(playerID)
            binding.root.findNavController().navigate(navAction)
        }

        viewModel.editCurrentYearEvent.observe(viewLifecycleOwner) {
            val year = it.getContentIfNotHandled() ?: return@observe

            val numberPickerView = requireActivity().layoutInflater.inflate(R.layout.layout_number_picker, null)
            val numberPicker = numberPickerView.findViewById<NumberPicker>(R.id.number_picker).apply {
                maxValue = YearPickerUtils.MAX_YEAR
                minValue = YearPickerUtils.MIN_YEAR
                value = year
            }
            val dialogBuilder = AlertDialog.Builder(binding.root.context)
                .setView(numberPickerView)
                .setPositiveButton(R.string.menu_edit_current_year_confirm) { _, _ ->
                    val number = numberPicker.value
                    viewModel.updateCurrentYear(number)
                }
                .setNegativeButton(R.string.menu_edit_current_year_cancel) { _, _ ->

                }

            dialogBuilder.show()
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

        viewModel.redirectToCreateSaveFileEvent.observe(viewLifecycleOwner) {
            if (it.hasBeenHandled) return@observe
            it.getContentIfNotHandled()
            val action = PlayerListFragmentDirections.actionPlayerListFragmentToSelectSaveFileFragment()
            findNavController().navigate(action)
        }

        viewModel.saveFileTitle.observe(viewLifecycleOwner) {
            requireActivity().findViewById<Toolbar>(R.id.toolbar).title = it
        }
    }

    private fun addViewListeners() {
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