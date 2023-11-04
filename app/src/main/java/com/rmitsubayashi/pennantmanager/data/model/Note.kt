package com.rmitsubayashi.pennantmanager.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rmitsubayashi.pennantmanager.ui.util.TimeUtil

@Entity
data class Note(
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "lastEdited")
    val lastEditedTimeStamp: Long,
    @PrimaryKey(autoGenerate = true)
    var id: Long = DEFAULT_ID
) {
    companion object {
        const val DEFAULT_ID = 0L

        fun default(): Note = Note(title = "", content = "", lastEditedTimeStamp = TimeUtil.currentTimestamp())
    }
}