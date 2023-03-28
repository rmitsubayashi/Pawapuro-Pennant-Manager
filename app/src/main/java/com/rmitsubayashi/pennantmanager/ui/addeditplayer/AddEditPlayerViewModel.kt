package com.rmitsubayashi.pennantmanager.ui.addeditplayer

import androidx.lifecycle.*
import com.rmitsubayashi.pennantmanager.data.repository.PlayerRepository
import com.rmitsubayashi.pennantmanager.data.model.Player
import com.rmitsubayashi.pennantmanager.data.model.Position
import com.rmitsubayashi.pennantmanager.ui.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AddEditPlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository
) : ViewModel() {
    private val _player = MutableLiveData(Player.default())
    private val _name = MutableLiveData("")
    private val _defaultName = MutableLiveData<String>()
    val defaultName: LiveData<String> = _defaultName
    private val _birthday = MutableLiveData(Player.DEFAULT_BIRTHDAY)
    val birthday: LiveData<LocalDate> = _birthday
    val birthdayLabel: LiveData<String> = _birthday.map {
            date -> DateTimeFormatter.ofPattern("yyyy/MM/dd").format(date)
    }
    private val _positions = MutableLiveData<Set<Position>>(emptySet())
    val positions: LiveData<Set<Position>> = _positions

    private val _savedEvent = MutableLiveData<Event<Unit>>()
    val savedEvent: LiveData<Event<Unit>> = _savedEvent

    private val _validationErrorEvent = MutableLiveData<Event<Unit>>()
    val validationErrorEvent: LiveData<Event<Unit>> = _validationErrorEvent

    fun fetchPlayer(playerId: Long) {
        viewModelScope.launch {
            val p = playerRepository.get(playerId)
            // has to come before other updates because
            // the other postValues can reset the player value to default
            _player.postValue(p)
            _name.postValue(p.name)
            _defaultName.postValue(p.name)
            _birthday.postValue(p.birthday)
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

    fun updateBirthday(newBirthday: LocalDate) {
        _birthday.postValue(newBirthday)
        val currentPlayer = _player.value ?: return
        _player.postValue(
            currentPlayer.copy(birthday = newBirthday)
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