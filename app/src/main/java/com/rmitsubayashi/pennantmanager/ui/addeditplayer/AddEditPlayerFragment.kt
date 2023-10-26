package com.rmitsubayashi.pennantmanager.ui.addeditplayer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.rmitsubayashi.pennantmanager.R
import com.rmitsubayashi.pennantmanager.data.model.GrowthType
import com.rmitsubayashi.pennantmanager.data.model.Position
import com.rmitsubayashi.pennantmanager.databinding.FragmentAddEditPlayerBinding
import com.rmitsubayashi.pennantmanager.ui.util.YearPickerUtils
import dagger.hilt.android.AndroidEntryPoint

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
        viewModel.fetchPlayer(args.playerId)
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

        viewModel.birthYear.observe(viewLifecycleOwner) {
            binding.playerBirthdayButton.text = it.toString()
        }

        viewModel.positions.observe(viewLifecycleOwner) {
            for (position in Position.values()) {
                val radioButton = mapPositionToRadioButton(position)
                radioButton.isChecked = it.contains(position)
            }
        }

        viewModel.growthType.observe(viewLifecycleOwner) {
            for (growthType in GrowthType.values()) {
                val radioButton = mapGrowthTypeToRadioButton(growthType)
                radioButton.isChecked = it == growthType
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
            val currentBirthday = viewModel.birthYear.value ?: return@setOnClickListener

            val numberPickerView = requireActivity().layoutInflater.inflate(R.layout.layout_number_picker, null)
            val numberPicker = numberPickerView.findViewById<NumberPicker>(R.id.number_picker).apply {
                maxValue = YearPickerUtils.MAX_YEAR
                minValue = YearPickerUtils.MIN_YEAR
                value = currentBirthday
            }
            val dialogBuilder = AlertDialog.Builder(binding.root.context)
                .setView(numberPickerView)
                .setPositiveButton(R.string.menu_edit_current_year_confirm) { _, _ ->
                    val number = numberPicker.value
                    viewModel.updateBirthYear(number)
                }
                .setNegativeButton(R.string.menu_edit_current_year_cancel) { _, _ ->

                }

            dialogBuilder.show()
        }

        for (position in Position.values()) {
            val radioButton = mapPositionToRadioButton(position)
            radioButton.setOnClickListener {
                viewModel.togglePosition(position)
            }
        }

        for (growthType in GrowthType.values()) {
            val radioButton = mapGrowthTypeToRadioButton(growthType)
            radioButton.setOnClickListener {
                viewModel.toggleGrowthType(growthType)
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

    private fun mapGrowthTypeToRadioButton(growthType: GrowthType): RadioButton {
        return when (growthType) {
            GrowthType.UNKNOWN -> binding.unknownRadioButton
            GrowthType.CHOU_SOUJUKU -> binding.chouSoujukuRadioButton
            GrowthType.SOUJUKU -> binding.soujukuRadioButton
            GrowthType.FUTSUU -> binding.futsuuRadioButton
            GrowthType.BANSEI -> binding.banseiRadioButton
            GrowthType.CHOU_BANSEI -> binding.chouBanseiRadioButton
        }
    }
}