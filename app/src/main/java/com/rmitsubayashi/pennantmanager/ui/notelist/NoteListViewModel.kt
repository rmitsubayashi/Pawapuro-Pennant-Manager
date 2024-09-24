package com.rmitsubayashi.pennantmanager.ui.notelist

import android.net.Uri
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmitsubayashi.pennantmanager.data.model.Note
import com.rmitsubayashi.pennantmanager.data.repository.NoteRepository
import com.rmitsubayashi.pennantmanager.data.repository.SaveFileRepository
import com.rmitsubayashi.pennantmanager.ui.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val saveFileRepository: SaveFileRepository,
    private val importExportHelper: ImportExportHelper
) : ViewModel() {
    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    private val _longClickListItemEvent = MutableLiveData<Note>()

    private val _lastRemovedNote = MutableLiveData<Event<Note>>()
    val lastRemovedNote: LiveData<Event<Note>> = _lastRemovedNote

    private val _addEditEvent = MutableLiveData<Event<Note?>>()
    val addEditEvent: LiveData<Event<Note?>> = _addEditEvent

    private val _importEvent = MutableLiveData<Event<Int>>()
    val importEvent: LiveData<Event<Int>> = _importEvent

    private val _exportEvent = MutableLiveData<Event<Int>>()
    val exportEvent: LiveData<Event<Int>> = _exportEvent

    fun fetchNoteList() {
        viewModelScope.launch {
            val currentSaveFile = saveFileRepository.getCurrentSaveFile() ?: return@launch
            val notes = noteRepository.getAll(currentSaveFile.id)
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

    fun export() {
        _notes.value?.let {
            val genericNotes = it.filter { note -> !note.isSaveFileNote() }
            importExportHelper.export(genericNotes)
            _exportEvent.postValue(Event(genericNotes.size))
        }
    }

    fun import(folderUri: Uri) {
        viewModelScope.launch {
            val pathSection = folderUri.lastPathSegment ?: return@launch
            // the section is {name of section}:value
            val path = pathSection.split(":")[1]
            val resultNotesSize = importExportHelper.import(File(Environment.getExternalStorageDirectory().path + "/" + path))
            _importEvent.postValue(Event(resultNotesSize))

            fetchNoteList()
        }
    }
}