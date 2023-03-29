package com.rmitsubayashi.pennantmanager.data.repository

import com.rmitsubayashi.pennantmanager.data.dao.PlayerDao
import com.rmitsubayashi.pennantmanager.data.model.Player
import javax.inject.Inject

class PlayerRepository @Inject constructor(private val playerDao: PlayerDao) {
    suspend fun add(player: Player) {
        playerDao.insert(player)
    }

    suspend fun update(player: Player) {
        playerDao.update(player)
    }

    suspend fun remove(player: Player) {
        playerDao.delete(player)
    }

    suspend fun getAll(saveFileId: Long): List<Player> {
        return playerDao.getAll(saveFileId)
    }

    suspend fun get(playerId: Long): Player {
        return playerDao.get(playerId)
    }
}