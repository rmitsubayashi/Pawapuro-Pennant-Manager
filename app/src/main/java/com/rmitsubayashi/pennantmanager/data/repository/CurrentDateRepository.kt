package com.rmitsubayashi.pennantmanager.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.rmitsubayashi.pennantmanager.data.model.CurrentDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CurrentDateRepository @Inject constructor(private val sharedPrefs: SharedPreferences) {
    private val defaultYear: CurrentDate = LocalDate.of(2022, 3, 25)

    fun get(): CurrentDate {
        val currentDateString = sharedPrefs.getString(SharedPrefsKeys.KEY_CURRENT_YEAR, "")
        if (currentDateString == "") {
            return defaultYear
        }
        return LocalDate.parse(currentDateString)
    }

    fun update(newDate: CurrentDate) {
        sharedPrefs.edit {
            putString(SharedPrefsKeys.KEY_CURRENT_YEAR, newDate.toString())
        }
    }
}