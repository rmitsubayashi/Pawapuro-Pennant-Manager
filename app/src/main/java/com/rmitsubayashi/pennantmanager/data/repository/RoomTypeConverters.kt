package com.rmitsubayashi.pennantmanager.data.repository

import androidx.room.TypeConverter
import com.rmitsubayashi.pennantmanager.data.model.Position
import java.time.LocalDate

class RoomTypeConverters {
    @TypeConverter
    fun fromPositionsToString(positions: Set<Position>): String {
        return positions.joinToString(",") {
            it.toString()
        }
    }

    @TypeConverter
    fun fromStringToPositions(positionString: String): Set<Position> {
        val positions = positionString.split(",")
        return positions.map { p -> Position.valueOf(p) }.toSet()
    }
}