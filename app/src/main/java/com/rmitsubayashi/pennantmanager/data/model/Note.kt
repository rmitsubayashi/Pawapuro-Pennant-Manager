package com.rmitsubayashi.pennantmanager.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.rmitsubayashi.pennantmanager.ui.util.TimeUtil

@Entity
data class Note(
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "saveFileId")
    val saveFileId: Long,
    @ColumnInfo(name = "lastEdited")
    val lastEditedTimeStamp: Long,
    @PrimaryKey(autoGenerate = true)
    var id: Long = DEFAULT_ID
) {
    companion object {
        const val DEFAULT_ID = 0L
        const val NO_SAVE_FILE_ID = -1L

        fun default(): Note = Note(title = "", content = "", saveFileId = NO_SAVE_FILE_ID, lastEditedTimeStamp = TimeUtil.currentTimestamp())

        fun defaultForSaveFile(saveFileName: String, saveFileId: Long): Note = Note(title = "${saveFileName}のメモ", content = "", saveFileId = saveFileId, lastEditedTimeStamp = TimeUtil.currentTimestamp())
    }
}