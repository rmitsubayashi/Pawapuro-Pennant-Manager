package com.rmitsubayashi.pennantmanager.ui.addeditnote

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rmitsubayashi.pennantmanager.R
import com.rmitsubayashi.pennantmanager.databinding.FragmentAddEditNoteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditNoteFragment : Fragment() {
    private val viewModel: AddEditNoteViewModel by viewModels()
    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!
    private val args: AddEditNoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchNote(args.noteId)
        addObservers()
        addViewListeners()
    }

    private fun addObservers() {

        viewModel.interactionMode.observe(viewLifecycleOwner) {
            when (it) {
                AddEditNoteViewModel.EDIT_MODE -> {
                    binding.saveButton.visibility = View.VISIBLE
                    binding.editButton.visibility = View.INVISIBLE

                    binding.titleEdittext.isEnabled = true
                    binding.titleEdittext.isFocusableInTouchMode = true
                    binding.contentEdittext.isEnabled = true
                    binding.contentEdittext.isFocusableInTouchMode = true
                }
                AddEditNoteViewModel.EDIT_CONTENT_ONLY_MODE -> {
                    binding.saveButton.visibility = View.VISIBLE
                    binding.editButton.visibility = View.INVISIBLE

                    binding.titleEdittext.isEnabled = false
                    binding.titleEdittext.isFocusableInTouchMode = false
                    binding.contentEdittext.isEnabled = true
                    binding.contentEdittext.isFocusableInTouchMode = true
                }
                AddEditNoteViewModel.VIEW_MODE -> {
                    binding.saveButton.visibility = View.INVISIBLE
                    binding.editButton.visibility = View.VISIBLE

                    // bug where the cursor stays on screen when scrolling?
                    binding.titleEdittext.clearFocus()
                    binding.titleEdittext.isEnabled = false
                    binding.titleEdittext.isFocusableInTouchMode = false
                    binding.contentEdittext.clearFocus()
                    binding.contentEdittext.isEnabled = false
                    binding.contentEdittext.isFocusableInTouchMode = false
                }
            }
        }

        viewModel.defaultTitle.observe(viewLifecycleOwner) {
            binding.titleEdittext.setText(it)
        }

        viewModel.defaultContent.observe(viewLifecycleOwner) {
            binding.contentEdittext.setText(it)
        }

        viewModel.edited.observe(viewLifecycleOwner) {
            binding.saveButton.isEnabled = it
        }

        viewModel.savedEvent.observe(viewLifecycleOwner) {
            if (it.hasBeenHandled) return@observe
            it.getContentIfNotHandled()
            Toast.makeText(requireContext(), R.string.saved_note, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addViewListeners() {
        binding.titleEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { viewModel.updateTitle(it.toString()) }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.contentEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { viewModel.updateContent(it.toString()) }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.saveButton.setOnClickListener {
            val inputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            requireActivity().currentFocus?.let {
                inputMethodManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)
            }

            viewModel.save()
        }

        binding.editButton.setOnClickListener {
            viewModel.startEdit()
        }

        val backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val edited = viewModel.edited.value
                if (edited == true) {
                    val dialogBuilder = AlertDialog.Builder(binding.root.context)
                        .setMessage(R.string.ask_to_save)
                        .setPositiveButton(R.string.ask_to_save_confirm) { _, _ ->
                            viewModel.save()
                            closeFragment()
                        }
                        .setNegativeButton(R.string.ask_to_save_deny) { _, _ ->
                            closeFragment()
                        }
                        .setNeutralButton(R.string.ask_to_save_cancel, null)

                    dialogBuilder.show()
                } else {
                    closeFragment()
                }
            }

            private fun closeFragment() {
                this.isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

        }

        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)

        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_edit_note_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // assumes only back arrow in menu
                val edited = viewModel.edited.value
                if (edited == true) {
                    val dialogBuilder = AlertDialog.Builder(binding.root.context)
                        .setMessage(R.string.ask_to_save)
                        .setPositiveButton(R.string.ask_to_save_confirm) { _, _ ->
                            viewModel.save()
                            this@AddEditNoteFragment.findNavController().navigateUp()
                        }
                        .setNegativeButton(R.string.ask_to_save_deny) { _, _ ->
                            this@AddEditNoteFragment.findNavController().navigateUp()
                        }
                        .setNeutralButton(R.string.ask_to_save_cancel, null)

                    dialogBuilder.show()
                } else {
                    return false
                }

                return true
            }
        }
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}