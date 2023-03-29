package com.rmitsubayashi.pennantmanager.ui.addeditplayer

import androidx.lifecycle.*
import com.rmitsubayashi.pennantmanager.data.repository.PlayerRepository
import com.rmitsubayashi.pennantmanager.data.model.Player
import com.rmitsubayashi.pennantmanager.data.model.Position
import com.rmitsubayashi.pennantmanager.data.repository.CurrentYearRepository
import com.rmitsubayashi.pennantmanager.data.repository.SaveFileRepository
import com.rmitsubayashi.pennantmanager.ui.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AddEditPlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val currentYearRepository: CurrentYearRepository,
    private val saveFileRepository: SaveFileRepository
) : ViewModel() {
    private val _player = MutableLiveData<Player>()
    private val _name = MutableLiveData("")
    private val _defaultName = MutableLiveData<String>()
    val defaultName: LiveData<String> = _defaultName
    private val _birthYear = MutableLiveData<Int>()
    val birthYear: LiveData<Int> = _birthYear
    private val _positions = MutableLiveData<Set<Position>>(emptySet())
    val positions: LiveData<Set<Position>> = _positions

    private val _savedEvent = MutableLiveData<Event<Unit>>()
    val savedEvent: LiveData<Event<Unit>> = _savedEvent

    private val _validationErrorEvent = MutableLiveData<Event<Unit>>()
    val validationErrorEvent: LiveData<Event<Unit>> = _validationErrorEvent

    fun fetchPlayer(playerId: Long) {
        if (playerId == Player.DEFAULT_ID) {
            viewModelScope.launch {
                val saveFile = saveFileRepository.getCurrentSaveFile() ?: return@launch // should not ever be null
                val currentYear = currentYearRepository.get()
                val estimateDraftPlayerYear = currentYear - 22
                _player.postValue(Player.default(saveFile.id).copy(birthYear = estimateDraftPlayerYear))
                _birthYear.postValue(estimateDraftPlayerYear)
            }
            return
        }
        viewModelScope.launch {
            val p = playerRepository.get(playerId)
            // has to come before other updates because
            // the other postValues can reset the player value to default
            _player.postValue(p)
            _name.postValue(p.name)
            _defaultName.postValue(p.name)
            _birthYear.postValue(p.birthYear)
            _positions.postValue(p.positions)
        }
    }

    fun togglePosition(position: Position) {
        val currentPositions = _positions.value?.toMutableSet() ?: return
        if (currentPositions.contains(position)) {
            currentPositions.remove(position)
        } else {
            currentPositions.add(position)
        }
        _positions.postValue(currentPositions)
        val currentPlayer = _player.value ?: return
        _player.postValue(
            currentPlayer.copy(positions = currentPositions)
        )
    }

    fun updateName(newName: String) {
        _name.postValue(newName)
        val currentPlayer = _player.value ?: return
        _player.postValue(
            currentPlayer.copy(name = newName)
        )
    }

    fun updateBirthYear(newBirthYear: Int) {
        _birthYear.postValue(newBirthYear)
        val currentPlayer = _player.value ?: return
        _player.postValue(
            currentPlayer.copy(birthYear = newBirthYear)
        )
    }

    fun save() {
        val currentPlayer = _player.value ?: return
        if (currentPlayer.name == "" || currentPlayer.positions.isEmpty()) {
            _validationErrorEvent.postValue(Event(Unit))
            return
        }
        viewModelScope.launch {
            if (currentPlayer.id == Player.DEFAULT_ID) {
                playerRepository.add(currentPlayer)
            } else {
                playerRepository.update(currentPlayer)
            }
            _savedEvent.postValue(Event(Unit))
        }
    }
}