package com.rmitsubayashi.pennantmanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rmitsubayashi.pennantmanager.data.model.SaveFile

@Dao
interface SaveFileDao {
    @Query("SELECT * FROM saveFile")
    suspend fun getAll(): List<SaveFile>

    @Query("SELECT * FROM saveFile WHERE id = :saveFileId")
    suspend fun get(saveFileId: Long): SaveFile

    @Insert
    suspend fun insert(saveFile: SaveFile): Long

    @Delete
    suspend fun delete(saveFile: SaveFile)

}