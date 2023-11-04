package com.rmitsubayashi.pennantmanager.ui.playerlist

import androidx.lifecycle.*
import com.rmitsubayashi.pennantmanager.data.model.GrowthType
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
    private val saveFileRepository: SaveFileRepository
) : ViewModel() {

    private val _saveFile = MutableLiveData<SaveFile>()
    val saveFileTitle: LiveData<String> = _saveFile.map { it.name }

    private val _players: LiveData<List<Player>> = _saveFile.switchMap {
        file -> liveData {
        val saveFileId = _saveFile.value?.id ?: return@liveData
            val players = playerRepository.getAll(saveFileId)
            val changedAge = players.map {
                // rough estimate. might be off by 1 year
                val newAge = file.currentYear - it.birthYear
                it.copy(age = newAge)
            }

            val changedRelativeAge = changedAge.map {
                val diff = when (it.growthType) {
                    GrowthType.UNKNOWN -> 0
                    GrowthType.CHOU_SOUJUKU -> 6
                    GrowthType.SOUJUKU -> 4
                    GrowthType.FUTSUU -> 0
                    GrowthType.BANSEI -> -3
                    GrowthType.CHOU_BANSEI -> -7
                }
                val relativeAge = it.age + diff
                it.copy(relativeAge = relativeAge)
            }
            val sortedByAge = changedRelativeAge.sortedByDescending { it.relativeAge }
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
            // player list will fetch automatically when save file is updated
        }
    }

    fun updateCurrentYear(newYear: Int) {
        viewModelScope.launch {
            saveFileRepository.updateCurrentYear(newYear)
            fetchPlayerList()
        }
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

    fun undoRemove(lastRemoved: Player) {
        viewModelScope.launch {
            playerRepository.add(lastRemoved)
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
        _saveFile.value ?.let {
            _editCurrentYearEvent.postValue(Event(it.currentYear))
        }
    }

    fun openFilter() {
        _filterState.value?.let {
            _openFilterEvent.postValue(Event(it))
        }
    }

}