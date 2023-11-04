package com.rmitsubayashi.pennantmanager.ui.notelist

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rmitsubayashi.pennantmanager.R
import com.rmitsubayashi.pennantmanager.data.model.Note
import com.rmitsubayashi.pennantmanager.databinding.FragmentNoteListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteListFragment : Fragment() {

    private val viewModel: NoteListViewModel by viewModels()
    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addToolbarMenu()
        viewModel.fetchNoteList()
        bindListAdapter()
        addObservers()
        addViewListeners()
    }

    private fun addToolbarMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.note_list_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_open_player_list -> {
                        val action = NoteListFragmentDirections.actionNoteListFragmentToPlayerListFragment()
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
            ID_REMOVE -> {
                viewModel.removeNote()
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun getContextMenuListener(): View.OnCreateContextMenuListener =
        View.OnCreateContextMenuListener {
            contextMenu, _, _ ->
            contextMenu.add(Menu.NONE, ID_REMOVE, Menu.NONE, requireContext().getString(R.string.remove_note_list_item))
        }

    private fun bindListAdapter() {
        binding.noteList.adapter = NoteListAdapter(viewLifecycleOwner, viewModel, getContextMenuListener())
    }

    private fun addObservers() {
        viewModel.lastRemovedNote.observe(viewLifecycleOwner) {
            if (it.hasBeenHandled) return@observe
            Snackbar.make(binding.root, R.string.undo_remove, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo_remove_confirm) {
                    viewModel.undoRemove()
                }
                .show()
        }

        viewModel.addEditEvent.observe(viewLifecycleOwner) {
            if (it.hasBeenHandled) return@observe
            val noteId = it.getContentIfNotHandled()?.id ?: Note.DEFAULT_ID
            val navAction = NoteListFragmentDirections.actionNoteListFragmentToAddEditNoteFragment(noteId)
            binding.root.findNavController().navigate(navAction)
        }
    }

    private fun addViewListeners() {
        binding.addNoteFab.setOnClickListener {
            viewModel.addNoteEvent()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ID_REMOVE = 0
    }

}