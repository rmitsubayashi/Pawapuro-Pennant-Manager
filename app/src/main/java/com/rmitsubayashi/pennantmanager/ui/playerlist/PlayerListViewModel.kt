package com.rmitsubayashi.pennantmanager.ui.playerlist

import androidx.lifecycle.*
import com.rmitsubayashi.pennantmanager.data.repository.CurrentDateRepository
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
    private val currentDateRepository: CurrentDateRepository
) : ViewModel() {

    private val _currentDate = MutableLiveData<CurrentDate>()
    val currentDate: LiveData<String> = _currentDate.map {
        date -> DateTimeFormatter.ofPattern("yyyy年MM月dd日").format(date)
    }

    private val _players: LiveData<List<Player>> = _currentDate.switchMap {
        date -> liveData {
            val players = playerRepository.get()
            val changedAge = players.map {
                val newAge = Period.between(it.birthday, date).years
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

    private val _editCurrentDateEvent = MutableLiveData<Event<CurrentDate>>()
    val editCurrentDateEvent: LiveData<Event<CurrentDate>> = _editCurrentDateEvent

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
        val currentDate = currentDateRepository.get()
        _currentDate.postValue(currentDate)
        // player list will fetch automatically when current date is updated
    }

    fun updateCurrentDate(newDate: LocalDate) {
        currentDateRepository.update(newDate)
        _currentDate.postValue(newDate)
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

    fun editCurrentDate() {
        _currentDate.value ?.let {
            _editCurrentDateEvent.postValue(Event(it))
        }

    }

    fun openFilter() {
        _filterState.value?.let {
            _openFilterEvent.postValue(Event(it))
        }
    }

}