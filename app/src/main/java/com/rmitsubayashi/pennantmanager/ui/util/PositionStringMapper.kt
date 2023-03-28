package com.rmitsubayashi.pennantmanager.ui.util

import com.rmitsubayashi.pennantmanager.R
import com.rmitsubayashi.pennantmanager.data.model.Position

object PositionStringMapper {
    fun mapPositionToStringRes(position: Position): Int {
        return when (position) {
            Position.STARTING_PITCHER -> R.string.starting_pitcher
            Position.RELIEVER -> R.string.reliever
            Position.CLOSER -> R.string.closer
            Position.CATCHER -> R.string.catcher
            Position.FIRST -> R.string.first
            Position.SHORTSTOP -> R.string.shortstop
            Position.SECOND -> R.string.second
            Position.THIRD -> R.string.third
            Position.OUTFIELDER -> R.string.outfield
        }
    }

    fun mapPositionToAbbreviationStringRes(position: Position): Int {
        return when (position) {
            Position.STARTING_PITCHER -> R.string.starting_pitcher_abbreviation
            Position.RELIEVER -> R.string.reliever_abbreviation
            Position.CLOSER -> R.string.closer_abbreviation
            Position.CATCHER -> R.string.catcher_abbreviation
            Position.FIRST -> R.string.first_abbreviation
            Position.SHORTSTOP -> R.string.shortstop_abbreviation
            Position.SECOND -> R.string.second_abbreviation
            Position.THIRD -> R.string.third_abbreviation
            Position.OUTFIELDER -> R.string.outfield_abbreviation
        }
    }
}