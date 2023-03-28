package com.rmitsubayashi.pennantmanager.ui.addeditplayer

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.rmitsubayashi.pennantmanager.R
import com.rmitsubayashi.pennantmanager.data.model.Position
import com.rmitsubayashi.pennantmanager.databinding.FragmentAddEditPlayerBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class AddEditPlayerFragment : Fragment() {
    private val viewModel: AddEditPlayerViewModel by viewModels()
    private var _binding: FragmentAddEditPlayerBinding? = null
    private val binding get() = _binding!!
    private val args: AddEditPlayerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.playerId != ARG_NEW_PLAYER_ID) {
            viewModel.fetchPlayer(args.playerId)
        }
        addObservers()
        addListeners()
    }

    private fun addObservers() {
        viewModel.savedEvent.observe(viewLifecycleOwner) {
            if (it.hasBeenHandled) return@observe
            binding.root.findNavController().navigate(
                AddEditPlayerFragmentDirections.actionAddEditPlayerFragmentToPlayerListFragment()
            )
        }

        viewModel.birthdayLabel.observe(viewLifecycleOwner) {
            binding.playerBirthdayButton.text = it
        }

        viewModel.positions.observe(viewLifecycleOwner) {
            for (position in Position.values()) {
                val radioButton = mapPositionToRadioButton(position)
                radioButton.isChecked = it.contains(position)
            }
        }

        viewModel.defaultName.observe(viewLifecycleOwner) {
            binding.playerNameEdittext.setText(it)
        }

        viewModel.validationErrorEvent.observe(viewLifecycleOwner) {
            if (it.hasBeenHandled) return@observe
            Toast.makeText(requireContext(), R.string.validation_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addListeners() {
        binding.playerBirthdayButton.setOnClickListener {
            val currentBirthday = viewModel.birthday.value ?: return@setOnClickListener
            DatePickerDialog(
                requireContext(),
                {
                        _, year, month, date ->
                    viewModel.updateBirthday(LocalDate.of(year, month + 1, date))
                },
                currentBirthday.year, currentBirthday.monthValue - 1, currentBirthday.dayOfMonth).show()
        }

        for (position in Position.values()) {
            val radioButton = mapPositionToRadioButton(position)
            radioButton.setOnClickListener {
                viewModel.togglePosition(position)
            }
        }

        binding.playerNameEdittext.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { viewModel.updateName(it.toString()) }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.saveButton.setOnClickListener {
            viewModel.save()
        }
    }

    private fun mapPositionToRadioButton(position: Position): RadioButton {
        return when (position) {
            Position.STARTING_PITCHER -> binding.startingPitcherRadioButton
            Position.RELIEVER -> binding.relieverRadioButton
            Position.CLOSER -> binding.closerRadioButton
            Position.CATCHER -> binding.catcherRadioButton
            Position.FIRST -> binding.firstRadioButton
            Position.SHORTSTOP -> binding.shortstopRadioButton
            Position.SECOND -> binding.secondRadioButton
            Position.THIRD -> binding.thirdRadioButton
            Position.OUTFIELDER -> binding.outfieldRadioButton
        }
    }



    companion object {
        const val ARG_NEW_PLAYER_ID = -1L
    }
}