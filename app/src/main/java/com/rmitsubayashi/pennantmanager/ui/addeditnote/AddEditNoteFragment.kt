package com.rmitsubayashi.pennantmanager.ui.addeditnote

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

        binding.contentEdittext.setOnScrollChangeListener { _, _, _, _, _ ->
            binding.contentEdittext.clearFocus()
        }

        binding.contentEdittext.setOnTouchListener(object : View.OnTouchListener {
            private var startClickTime = 0L
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    startClickTime = System.currentTimeMillis()
                } else if (event.action == MotionEvent.ACTION_UP) {
                    val isScroll = System.currentTimeMillis() - startClickTime > ViewConfiguration.getTapTimeout()
                    if (isScroll) {
                        binding.contentEdittext.startScrollerTask()
                    }
                }

                return false
            }
        })

        binding.contentEdittext.setOnScrollStoppedListener {
            binding.contentEdittext.clearFocus()
        }

        binding.saveButton.setOnClickListener {
            viewModel.save()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}