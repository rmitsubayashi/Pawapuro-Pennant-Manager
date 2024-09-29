package com.rmitsubayashi.pennantmanager

import androidx.lifecycle.ViewModel
import com.rmitsubayashi.pennantmanager.data.repository.SaveFileRepository
import com.rmitsubayashi.pennantmanager.ui.util.TeamStyleMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val saveFileRepository: SaveFileRepository
) : ViewModel() {

    var previousToolbarColor: Int? = null
        private set
    var currentToolbarColor: Int? = null
        private set
    var previousSystemBarColor: Int? = null
        private set
    var currentSystemBarColor: Int? = null
        private set

    suspend fun getThemeId() : Int {
        val currentSaveFile = saveFileRepository.getCurrentSaveFile()
        return TeamStyleMapper.map(currentSaveFile?.name ?: "")
    }

    fun setCurrentThemeColors(toolbarColor: Int, systemBarColor: Int) {
        currentToolbarColor = toolbarColor
        currentSystemBarColor = systemBarColor
    }

    fun hasThemeChanged(): Boolean {
        if (previousToolbarColor ==null || currentToolbarColor == null) return false
        return previousToolbarColor != currentToolbarColor
    }

    fun finishUpdatingTheme() {
        previousToolbarColor = currentToolbarColor
        previousSystemBarColor = currentSystemBarColor
        currentToolbarColor = null
        currentSystemBarColor = null
    }
}