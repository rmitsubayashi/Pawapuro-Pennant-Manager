package com.rmitsubayashi.pennantmanager.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SaveFile(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "currentYear")
    val currentYear: Int
) {
    companion object {
        fun create(name: String): SaveFile = SaveFile(0, name, 2022)
    }
}
