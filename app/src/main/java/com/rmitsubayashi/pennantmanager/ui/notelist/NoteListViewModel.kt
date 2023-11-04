package com.rmitsubayashi.pennantmanager.ui.notelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmitsubayashi.pennantmanager.data.model.Note
import com.rmitsubayashi.pennantmanager.data.repository.NoteRepository
import com.rmitsubayashi.pennantmanager.ui.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {
    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    private val _longClickListItemEvent = MutableLiveData<Note>()

    private val _lastRemovedNote = MutableLiveData<Event<Note>>()
    val lastRemovedNote: LiveData<Event<Note>> = _lastRemovedNote

    private val _addEditEvent = MutableLiveData<Event<Note?>>()
    val addEditEvent: LiveData<Event<Note?>> = _addEditEvent

    fun fetchNoteList() {
        viewModelScope.launch {
            val notes = noteRepository.getAll()
            val notesByLastEdited = notes.sortedByDescending { it.lastEditedTimeStamp }

            _notes.postValue(notesByLastEdited)
        }
    }

    fun longClickListItem(note: Note) {
        _longClickListItemEvent.postValue(note)
    }

    fun removeNote() {
        val targetNote = _longClickListItemEvent.value ?: return
        viewModelScope.launch {
            noteRepository.remove(targetNote)
            _lastRemovedNote.postValue(Event(targetNote))
            fetchNoteList()

        }
    }

    fun undoRemove(lastRemoved: Note) {
        viewModelScope.launch {
            noteRepository.add(lastRemoved)
            fetchNoteList()
        }
    }

    fun viewNote(note: Note) {
        _addEditEvent.postValue(Event(note))
    }

    fun addNoteEvent() {
        _addEditEvent.postValue(Event(null))
    }
}