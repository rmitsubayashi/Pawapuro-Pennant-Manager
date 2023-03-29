package com.rmitsubayashi.pennantmanager.ui.playerlist

import androidx.lifecycle.*
import com.rmitsubayashi.pennantmanager.data.repository.CurrentYearRepository
import com.rmitsubayashi.pennantmanager.data.repository.PlayerRepository
import com.rmitsubayashi.pennantmanager.data.model.Player
import com.rmitsubayashi.pennantmanager.data.model.SaveFile
import com.rmitsubayashi.pennantmanager.data.repository.SaveFileRepository
import com.rmitsubayashi.pennantmanager.ui.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerListViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val currentYearRepository: CurrentYearRepository,
    private val saveFileRepository: SaveFileRepository
) : ViewModel() {

    private val _saveFile = MutableLiveData<SaveFile>()
    val saveFileTitle: LiveData<String> = _saveFile.map { it.name }

    private val _currentYear = MutableLiveData<Int>()

    private val _players: LiveData<List<Player>> = _currentYear.switchMap {
        year -> liveData {
        val saveFileId = _saveFile.value?.id ?: return@liveData
            val players = playerRepository.getAll(saveFileId)
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

    private val _redirectToCreateSaveFileEvent = MutableLiveData<Event<Unit>>()
    val redirectToCreateSaveFileEvent: LiveData<Event<Unit>> = _redirectToCreateSaveFileEvent

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
        // check for a save file first. if no save file, redirect the user to create save file
        viewModelScope.launch {
            val saveFile = saveFileRepository.getCurrentSaveFile()
            if (saveFile == null) {
                _redirectToCreateSaveFileEvent.postValue(Event(Unit))
                return@launch
            }
            _saveFile.postValue(saveFile)

            val currentYear = currentYearRepository.get()
            _currentYear.postValue(currentYear)
            // player list will fetch automatically when current date is updated
        }
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