package com.rmitsubayashi.pennantmanager.data.dao

import androidx.room.*
import com.rmitsubayashi.pennantmanager.data.model.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM note WHERE saveFileId in (:saveFileId, " + Note.NO_SAVE_FILE_ID + ")")
    suspend fun getAll(saveFileId: Long): List<Note>

    @Query("SELECT * FROM note WHERE id = :noteId")
    suspend fun get(noteId: Long): Note

    @Insert
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("DELETE FROM note WHERE saveFileId = :saveFileId")
    suspend fun deleteBySaveFileId(saveFileId: Long)

    @Update(entity = Note::class)
    suspend fun update(vararg note: Note)


}