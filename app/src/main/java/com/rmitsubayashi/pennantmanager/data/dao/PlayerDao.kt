package com.rmitsubayashi.pennantmanager.data.dao

import androidx.room.*
import com.rmitsubayashi.pennantmanager.data.model.Player

@Dao
interface PlayerDao {
    @Query("SELECT * FROM player")
    suspend fun getAll(): List<Player>

    @Query("SELECT * FROM player WHERE id = :playerId")
    suspend fun get(playerId: Long): Player

    @Insert
    suspend fun insert(player: Player)

    @Delete
    suspend fun delete(player: Player)

    @Update(entity = Player::class)
    suspend fun update(vararg player: Player)
}