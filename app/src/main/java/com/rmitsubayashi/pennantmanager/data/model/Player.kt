package com.rmitsubayashi.pennantmanager.data.model

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import java.time.LocalDate
import java.util.*

@Entity(foreignKeys = [ForeignKey(onDelete = CASCADE, entity = SaveFile::class, parentColumns = arrayOf("id"), childColumns = arrayOf("saveFileId"))])
data class Player(
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "birthYear")
    val birthYear: Int,
    @ColumnInfo(name = "positions")
    val positions: Set<Position>,
    @ColumnInfo(name = "growthType")
    val growthType: GrowthType,
    @ColumnInfo(name = "saveFileId")
    val saveFileId: Long,
    @PrimaryKey(autoGenerate = true)
    var id: Long = DEFAULT_ID,
    @Ignore
    val age: Int = -1, // will be calculated later when we know the current date
    @Ignore
    val relativeAge: Int = -1 // will be calculated based on age and growth type
) {
    constructor(name: String, birthYear: Int, positions: Set<Position>, growthType: GrowthType, saveFileId: Long, id: Long) : this(name, birthYear, positions, growthType, saveFileId, id, -1)

    companion object {
        const val DEFAULT_ID = 0L

        private const val DEFAULT_BIRTH_YEAR: Int = 1996

        fun default(saveFileId: Long): Player = Player(name = "", birthYear = DEFAULT_BIRTH_YEAR, positions = emptySet(), growthType = GrowthType.UNKNOWN, saveFileId = saveFileId)
    }
}