package com.rmitsubayashi.pennantmanager.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.*

@Entity
data class Player(
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "birthYear")
    val birthYear: Int,
    @ColumnInfo(name = "positions")
    val positions: Set<Position>,
    @PrimaryKey(autoGenerate = true)
    var id: Long = DEFAULT_ID,
    @Ignore
    val age: Int = -1 // will be calculated later when we know the current date
) {
    constructor(name: String, birthYear: Int, positions: Set<Position>, id: Long) : this(name, birthYear, positions, id, -1)

    companion object {
        const val DEFAULT_ID = 0L

        const val DEFAULT_BIRTH_YEAR: Int = 1996

        fun default(): Player = Player(name = "", birthYear = DEFAULT_BIRTH_YEAR, positions = emptySet())
    }
}