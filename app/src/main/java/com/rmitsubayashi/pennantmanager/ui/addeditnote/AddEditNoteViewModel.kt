package com.rmitsubayashi.pennantmanager.ui.addeditnote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmitsubayashi.pennantmanager.data.model.Note
import com.rmitsubayashi.pennantmanager.data.repository.NoteRepository
import com.rmitsubayashi.pennantmanager.ui.util.Event
import com.rmitsubayashi.pennantmanager.ui.util.TimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {
    private val _note = MutableLiveData<Note>()
    private val _title = MutableLiveData("")
    private val _defaultTitle = MutableLiveData<String>()
    val defaultTitle: LiveData<String> = _defaultTitle
    private val _content = MutableLiveData("")
    private val _defaultContent = MutableLiveData<String>()
    val defaultContent: LiveData<String> = _defaultContent
    private val _edited = MutableLiveData(false)
    val edited: LiveData<Boolean> = _edited
    private val _savedEvent = MutableLiveData<Event<Unit>>()
    val savedEvent: LiveData<Event<Unit>> = _savedEvent
    private val _interactionMode = MutableLiveData(VIEW_MODE)
    val interactionMode: LiveData<Int> = _interactionMode


    fun fetchNote(noteId: Long) {
        if (noteId == Note.DEFAULT_ID) {
            _note.postValue(Note.default())
            _interactionMode.postValue(EDIT_MODE)
            return
        }

        viewModelScope.launch {
            val n = noteRepository.get(noteId)
            _note.postValue(n)
            _title.postValue(n.title)
            _defaultTitle.postValue(n.title)
            _content.postValue(n.content)
            _defaultContent.postValue(n.content)
        }
    }

    fun updateTitle(newTitle: String) {
        _title.postValue(newTitle)
        val currentNote = _note.value ?: return
        _note.postValue(
            currentNote.copy(title = newTitle)
        )
        // prevents initial update from triggering
        if (_defaultTitle.value != newTitle) {
            _edited.postValue(true)
        }
    }

    fun updateContent(newContent: String) {
        _content.postValue(newContent)
        val currentNote = _note.value ?: return
        _note.postValue(
            currentNote.copy(content = newContent)
        )
        // prevents initial update from triggering
        if (_defaultContent.value != newContent) {
            _edited.postValue(true)
        }
    }

    fun startEdit() {
        _interactionMode.postValue(EDIT_MODE)
    }

    fun save() {
        val currentNote = _note.value ?: return
        val currentNoteWithNewTimestamp = currentNote.copy(lastEditedTimeStamp = TimeUtil.currentTimestamp())
        viewModelScope.launch {
            if (currentNote.id == Note.DEFAULT_ID) {
                noteRepository.add(currentNoteWithNewTimestamp)
            } else {
                noteRepository.update(currentNoteWithNewTimestamp)
            }
            _edited.postValue(false)
            _savedEvent.postValue(Event(Unit))
            _interactionMode.postValue(VIEW_MODE)
        }
    }

    companion object {
        const val VIEW_MODE = 0
        const val EDIT_MODE = 1
    }
}