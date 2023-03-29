package com.rmitsubayashi.pennantmanager.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.rmitsubayashi.pennantmanager.data.dao.SaveFileDao
import com.rmitsubayashi.pennantmanager.data.model.SaveFile
import javax.inject.Inject

class SaveFileRepository @Inject constructor(
    private val saveFileDao: SaveFileDao,
    private val sharedPrefs: SharedPreferences
) {
    suspend fun add(saveFile: SaveFile): Long {
        return saveFileDao.insert(saveFile)
    }

    suspend fun remove(saveFile: SaveFile) {
        saveFileDao.delete(saveFile)
    }

    suspend fun getAll(): List<SaveFile> {
        return saveFileDao.getAll()
    }

    suspend fun getCurrentSaveFile(): SaveFile? {
        val currentId = sharedPrefs.getLong(SharedPrefsKeys.KEY_CURRENT_SAVE_FILE_ID, -1)
        if (currentId == -1L) return null
        return saveFileDao.get(currentId)
    }

    fun setCurrentSaveFile(saveFileId: Long) {
        sharedPrefs.edit {
            putLong(SharedPrefsKeys.KEY_CURRENT_SAVE_FILE_ID, saveFileId)
        }
    }

    suspend fun updateCurrentYear(newYear: Int) {
        val currentSaveFile = getCurrentSaveFile() ?: return
        saveFileDao.update(currentSaveFile.copy(currentYear = newYear))
    }
}