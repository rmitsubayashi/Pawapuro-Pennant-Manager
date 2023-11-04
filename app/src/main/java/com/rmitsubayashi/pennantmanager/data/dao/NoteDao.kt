package com.rmitsubayashi.pennantmanager.data.dao

import androidx.room.*
import com.rmitsubayashi.pennantmanager.data.model.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    suspend fun getAll(): List<Note>

    @Query("SELECT * FROM note WHERE id = :noteId")
    suspend fun get(noteId: Long): Note

    @Insert
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Update(entity = Note::class)
    suspend fun update(vararg note: Note)


}