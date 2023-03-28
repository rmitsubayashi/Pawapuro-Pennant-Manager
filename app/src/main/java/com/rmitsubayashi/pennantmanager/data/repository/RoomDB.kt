package com.rmitsubayashi.pennantmanager.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rmitsubayashi.pennantmanager.data.dao.PlayerDao
import com.rmitsubayashi.pennantmanager.data.model.Player

@Database(entities = [Player::class], version = 1)
@TypeConverters(RoomTypeConverters::class)
abstract class RoomDB: RoomDatabase() {
    abstract fun playerDao(): PlayerDao
}