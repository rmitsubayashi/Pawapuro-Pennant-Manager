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
    @ColumnInfo(name = "birthday")
    val birthday: LocalDate,
    @ColumnInfo(name = "positions")
    val positions: Set<Position>,
    @PrimaryKey(autoGenerate = true)
    var id: Long = DEFAULT_ID,
    @Ignore
    val age: Int = -1 // will be calculated later when we know the current date
) {
    constructor(name: String, birthday: LocalDate, positions: Set<Position>, id: Long) : this(name, birthday, positions, id, -1)

    companion object {
        const val DEFAULT_ID = 0L

        val DEFAULT_BIRTHDAY: LocalDate = LocalDate.of(1996,1,1)

        fun default(): Player = Player(name = "", birthday = DEFAULT_BIRTHDAY, positions = emptySet())
    }
}