package com.rmitsubayashi.pennantmanager.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class CurrentYearRepository @Inject constructor(private val sharedPrefs: SharedPreferences) {
    private val defaultYear: Int = 2022

    fun get(): Int {
        return sharedPrefs.getInt(SharedPrefsKeys.KEY_CURRENT_YEAR, defaultYear)
    }

    fun update(newYear: Int) {
        sharedPrefs.edit {
            putInt(SharedPrefsKeys.KEY_CURRENT_YEAR, newYear)
        }
    }
}