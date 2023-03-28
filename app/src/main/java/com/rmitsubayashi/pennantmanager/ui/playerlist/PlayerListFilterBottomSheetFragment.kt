package com.rmitsubayashi.pennantmanager.ui.playerlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rmitsubayashi.pennantmanager.data.model.Position
import com.rmitsubayashi.pennantmanager.databinding.LayoutFilterBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerListFilterBottomSheetFragment : BottomSheetDialogFragment() {
    private val viewModel: PlayerListFilterViewModel by viewModels()
    private var _binding: LayoutFilterBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var initialFilterState: FilterState
    private lateinit var onFilterSelectedListener: PlayerListFragment.OnFilterSelectedListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = LayoutFilterBottomSheetBinding.inflate(inflater, container, false)
        setButtonListeners()
        setObservers()
        viewModel.presetFilterState(initialFilterState)
        return binding.root
    }

    private fun setButtonListeners() {
        binding.fieldersButton.setOnClickListener {
            viewModel.toggleFielders()
        }
        binding.pitchersButton.setOnClickListener {
            viewModel.togglePitchers()
        }

        for (position in Position.values()) {
            val radioButton = mapPositionToRadioButton(position)
            radioButton.setOnClickListener {
                viewModel.togglePositionFilter(position)
            }
        }
    }

    private fun setObservers() {
        viewModel.filterState.observe(viewLifecycleOwner) {
            for (position in Position.values()) {
                val radioButton = mapPositionToRadioButton(position)
                radioButton.isChecked = it.positions.contains(position)
            }

            onFilterSelectedListener.onFilterSelected(it)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(filterState: FilterState, onFilterSelectedListener: PlayerListFragment.OnFilterSelectedListener): PlayerListFilterBottomSheetFragment {
            return PlayerListFilterBottomSheetFragment().apply {
                initialFilterState = filterState
                this.onFilterSelectedListener = onFilterSelectedListener
            }
        }
    }
}