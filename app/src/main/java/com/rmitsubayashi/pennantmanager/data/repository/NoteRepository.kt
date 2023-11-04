package com.rmitsubayashi.pennantmanager.data.repository

import com.rmitsubayashi.pennantmanager.data.dao.NoteDao
import com.rmitsubayashi.pennantmanager.data.model.Note
import javax.inject.Inject

class NoteRepository @Inject constructor(private val noteDao: NoteDao) {
    suspend fun add(note: Note) {
        noteDao.insert(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note)
    }

    suspend fun remove(note: Note) {
        noteDao.delete(note)
    }

    suspend fun getAll(): List<Note> {
        return noteDao.getAll()
    }

    suspend fun get(noteId: Long): Note {
        return noteDao.get(noteId)
    }
}