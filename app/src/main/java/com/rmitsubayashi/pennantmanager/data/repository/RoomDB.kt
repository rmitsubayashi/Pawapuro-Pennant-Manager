package com.rmitsubayashi.pennantmanager.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rmitsubayashi.pennantmanager.data.dao.NoteDao
import com.rmitsubayashi.pennantmanager.data.dao.PlayerDao
import com.rmitsubayashi.pennantmanager.data.dao.SaveFileDao
import com.rmitsubayashi.pennantmanager.data.model.Note
import com.rmitsubayashi.pennantmanager.data.model.Player
import com.rmitsubayashi.pennantmanager.data.model.SaveFile

@Database(entities = [Player::class, SaveFile::class, Note::class], version = 1)
@TypeConverters(RoomTypeConverters::class)
abstract class RoomDB: RoomDatabase() {
    abstract fun playerDao(): PlayerDao

    abstract fun saveFileDao(): SaveFileDao

    abstract fun noteDao(): NoteDao
}