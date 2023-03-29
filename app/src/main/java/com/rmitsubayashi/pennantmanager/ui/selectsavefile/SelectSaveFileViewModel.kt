package com.rmitsubayashi.pennantmanager.ui.selectsavefile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmitsubayashi.pennantmanager.data.model.SaveFile
import com.rmitsubayashi.pennantmanager.data.repository.SaveFileRepository
import com.rmitsubayashi.pennantmanager.ui.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectSaveFileViewModel @Inject constructor(
    private val saveFileRepository: SaveFileRepository
) : ViewModel() {

    private val _saveFiles = MutableLiveData<List<SaveFile>>()
    val saveFiles: LiveData<List<SaveFile>> = _saveFiles

    private val _saveFileSelectedOnScreen = MutableLiveData<SaveFile>()
    val saveFileSelectedOnScreen: LiveData<SaveFile> = _saveFileSelectedOnScreen

    private val _fileSelectedEvent = MutableLiveData<Event<Unit>>()
    val fileSelectedEvent: LiveData<Event<Unit>> = _fileSelectedEvent

    private val _fileDeletedEvent = MutableLiveData<Event<SaveFile>>()
    val fileDeletedEvent: LiveData<Event<SaveFile>> = _fileDeletedEvent

    private val _addFileErrorEvent = MutableLiveData<Event<Unit>>()
    val addFileErrorEvent: LiveData<Event<Unit>> = _addFileErrorEvent

    fun fetchSaveFiles() {
        viewModelScope.launch {
            val files = saveFileRepository.getAll()
            _saveFiles.postValue(files)
        }
    }

    fun createSaveFile(name: String) {
        if (name == "") {
            _addFileErrorEvent.postValue(Event(Unit))
            return
        }
        val allSaveFiles = _saveFiles.value
        if (allSaveFiles == null){
            _addFileErrorEvent.postValue(Event(Unit))
            return
        }
        if (allSaveFiles.find { it.name == name } != null) {
            _addFileErrorEvent.postValue(Event(Unit))
            return
        }

        viewModelScope.launch {
            val saveFileId = saveFileRepository.add(SaveFile(0, name))
            saveFileRepository.setCurrentSaveFile(saveFileId)
            _fileSelectedEvent.postValue(Event(Unit))
        }
    }

    fun selectSaveFile(saveFile: SaveFile) {
        _saveFileSelectedOnScreen.postValue(saveFile)
    }

    fun confirmSelectSaveFile() {
        val selectedSaveFile = _saveFileSelectedOnScreen.value ?: return
        saveFileRepository.setCurrentSaveFile(selectedSaveFile.id)
        _fileSelectedEvent.postValue(Event(Unit))
    }

    fun confirmDeleteSaveFile() {
        val selectedSaveFile = _saveFileSelectedOnScreen.value ?: return
        viewModelScope.launch {
            saveFileRepository.remove(selectedSaveFile)
            _fileDeletedEvent.postValue(Event(selectedSaveFile))
            fetchSaveFiles()
        }
    }
}