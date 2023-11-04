package com.rmitsubayashi.pennantmanager.ui.selectsavefile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.rmitsubayashi.pennantmanager.R
import com.rmitsubayashi.pennantmanager.databinding.FragmentSelectSaveFileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectSaveFileFragment : Fragment() {

    private val viewModel: SelectSaveFileViewModel by viewModels()
    private var _binding: FragmentSelectSaveFileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectSaveFileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchSaveFiles()
        bindListAdapter()
        addObservers()
        addViewListeners()
    }

    private fun bindListAdapter() {
        binding.saveFileList.adapter = SaveFileListAdapter(viewLifecycleOwner, viewModel)
    }

    private fun addObservers() {
        viewModel.fileSelectedEvent.observe(viewLifecycleOwner) {
            if (it.hasBeenHandled) return@observe
            it.getContentIfNotHandled()
            val navAction = SelectSaveFileFragmentDirections.actionSelectSaveFileFragmentToPlayerListFragment()
            binding.root.findNavController().navigate(navAction)
        }

        viewModel.fileDeletedEvent.observe(viewLifecycleOwner) {
            val saveFile = it.getContentIfNotHandled() ?: return@observe
            Toast.makeText(binding.root.context, getString(R.string.save_file_deleted, saveFile.name), Toast.LENGTH_SHORT).show()
        }

        viewModel.addFileErrorEvent.observe(viewLifecycleOwner) {
            if (it.hasBeenHandled) return@observe
            it.getContentIfNotHandled()
            Toast.makeText(binding.root.context, R.string.validation_error, Toast.LENGTH_SHORT).show()
        }

        viewModel.saveFileSelectedOnScreen.observe(viewLifecycleOwner) {
            binding.selectSaveFileButton.isEnabled = true
            binding.deleteSaveFileButton.isEnabled = true
        }
    }

    private fun addViewListeners() {
        binding.addSaveFileButton.setOnClickListener {
            val editTextView = requireActivity().layoutInflater.inflate(R.layout.layout_edit_text, null)
            val editText = editTextView.findViewById<EditText>(R.id.edittext)
            AlertDialog.Builder(binding.root.context)
                .setView(editTextView)
                .setTitle(R.string.add_save_file)
                .setPositiveButton(R.string.add_save_file_confirm) { _, _ ->
                    viewModel.createSaveFile(editText.text.toString())
                }
                .setNegativeButton(R.string.add_save_file_cancel) { _, _ ->

                }
                .show()
        }

        binding.selectSaveFileButton.setOnClickListener {
            viewModel.confirmSelectSaveFile()
        }

        binding.deleteSaveFileButton.setOnClickListener {
            AlertDialog.Builder(binding.root.context)
                .setTitle(R.string.delete_save_file_confirmation)
                .setPositiveButton(R.string.delete_save_file_yes) { _, _ ->
                    viewModel.confirmDeleteSaveFile()
                }
                .setNegativeButton(R.string.delete_save_file_no) { _, _ ->

                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}