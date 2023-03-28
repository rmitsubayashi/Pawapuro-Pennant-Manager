package com.rmitsubayashi.pennantmanager.ui.playerlist

import androidx.lifecycle.*
import com.rmitsubayashi.pennantmanager.data.repository.CurrentYearRepository
import com.rmitsubayashi.pennantmanager.data.repository.PlayerRepository
import com.rmitsubayashi.pennantmanager.data.model.CurrentDate
import com.rmitsubayashi.pennantmanager.data.model.Player
import com.rmitsubayashi.pennantmanager.ui.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class PlayerListViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val currentYearRepository: CurrentYearRepository
) : ViewModel() {

    private val _currentYear = MutableLiveData<Int>()

    private val _players: LiveData<List<Player>> = _currentYear.switchMap {
        year -> liveData {
            val players = playerRepository.get()
            val changedAge = players.map {
                // rough estimate. might be off by 1 year
                val newAge = year - it.birthYear
                it.copy(age = newAge)
            }
            val sortedByAge = changedAge.sortedByDescending { it.age }
            emit(sortedByAge)
        }
    }

    private val _filterState = MutableLiveData(FilterState(emptySet()))

    private val _filteredPlayers = MediatorLiveData<List<Player>>()

    val players : LiveData<List<Player>> = _filteredPlayers

    private val _longClickListItemEvent = MutableLiveData<Player>()

    private val _lastRemovedPlayer = MutableLiveData<Event<Player>>()
    val lastRemovedPlayer: LiveData<Event<Player>> = _lastRemovedPlayer

    private val _addEditEvent = MutableLiveData<Event<Player?>>()
    val addEditEvent: LiveData<Event<Player?>> = _addEditEvent

    private val _openFilterEvent = MutableLiveData<Event<FilterState>>()
    val openFilterEvent: LiveData<Event<FilterState>> = _openFilterEvent

    private val _editCurrentYearEvent = MutableLiveData<Event<Int>>()
    val editCurrentYearEvent: LiveData<Event<Int>> = _editCurrentYearEvent

    init {
        initFilteredPlayerListSources()
    }



    private fun initFilteredPlayerListSources() {
        _filteredPlayers.addSource(_players) {
            val currentFilter = _filterState.value ?: return@addSource // assumes never null
            val filtered = applyPlayerFilter(it, currentFilter)
            _filteredPlayers.postValue(filtered)
        }

        _filteredPlayers.addSource(_filterState) {
            val currentPlayers = _players.value ?: return@addSource
            val filtered = applyPlayerFilter(currentPlayers, it)
            _filteredPlayers.postValue(filtered)
        }
    }


    fun fetchPlayerList() {
        val currentYear = currentYearRepository.get()
        _currentYear.postValue(currentYear)
        // player list will fetch automatically when current date is updated
    }

    fun updateCurrentYear(newYear: Int) {
        currentYearRepository.update(newYear)
        _currentYear.postValue(newYear)
    }

    fun updateFilter(newFilter: FilterState) {
        _filterState.postValue(newFilter)
    }

    private fun applyPlayerFilter(players: List<Player>, filterState: FilterState): List<Player> {
        if (filterState.positions.isEmpty()) {
            return players
        }
        return players.toMutableList().filter {
            it.positions.intersect(filterState.positions).isNotEmpty()
        }
    }

    fun longClickListItem(player: Player) {
        _longClickListItemEvent.postValue(player)
    }

    fun removePlayer() {
        val targetPlayer = _longClickListItemEvent.value ?: return
        viewModelScope.launch {
            playerRepository.remove(targetPlayer)
            _lastRemovedPlayer.postValue(Event(targetPlayer))
            fetchPlayerList()
        }
    }

    fun undoRemove() {
        val lastRemoved = _lastRemovedPlayer.value ?: return
        viewModelScope.launch {
            playerRepository.add(lastRemoved.peekContent())
            fetchPlayerList()
        }
    }

    fun editPlayer() {
        val targetPlayer = _longClickListItemEvent.value ?: return
        _addEditEvent.postValue(Event(targetPlayer))
    }

    fun addPlayerEvent() {
        _addEditEvent.postValue(Event(null))
    }

    fun editCurrentYear() {
        _currentYear.value ?.let {
            _editCurrentYearEvent.postValue(Event(it))
        }

    }

    fun openFilter() {
        _filterState.value?.let {
            _openFilterEvent.postValue(Event(it))
        }
    }

}