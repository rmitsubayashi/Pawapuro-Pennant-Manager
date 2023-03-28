package com.rmitsubayashi.pennantmanager.ui.playerlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rmitsubayashi.pennantmanager.data.model.Position
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerListFilterViewModel @Inject constructor(): ViewModel() {
    private val _positionFilter = MutableLiveData<Set<Position>>()
    private val _filterState = MediatorLiveData(FilterState(emptySet()))
    val filterState: LiveData<FilterState> = _filterState
    private val _submittedFilterState = MutableLiveData<FilterState>()
    val submittedFilterState: LiveData<FilterState> = _submittedFilterState

    init {
        initFilterStateSources()
    }

    private fun initFilterStateSources() {
        _filterState.addSource(_positionFilter) {
            val currentFilterState = _filterState.value ?: return@addSource
            val newFilterState = currentFilterState.copy(positions = it)
            _filterState.postValue(newFilterState)
        }
    }

    fun presetFilterState(initialFilterState: FilterState) {
        _positionFilter.value = initialFilterState.positions
    }

    fun togglePositionFilter(position: Position) {
        val currentPositions = _positionFilter.value?.toMutableSet() ?: return
        if (currentPositions.contains(position)) {
            currentPositions.remove(position)
        } else {
            currentPositions.add(position)
        }
        _positionFilter.postValue(currentPositions)
    }

    fun togglePitchers() {
        _positionFilter.postValue(setOf(Position.STARTING_PITCHER, Position.RELIEVER, Position.CLOSER))
    }

    fun toggleFielders() {
        _positionFilter.postValue(setOf(Position.CATCHER, Position.FIRST, Position.SECOND, Position.SHORTSTOP, Position.THIRD, Position.OUTFIELDER))
    }

    fun submit() {
        _filterState.value?.let {
            _submittedFilterState.postValue(it)
        }
    }
}