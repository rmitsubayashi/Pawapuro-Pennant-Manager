package com.rmitsubayashi.pennantmanager.data.dao

import androidx.room.*
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

    @Update(entity = SaveFile::class)
    suspend fun update(vararg saveFile: SaveFile)

}