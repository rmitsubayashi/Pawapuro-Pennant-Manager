package com.rmitsubayashi.pennantmanager.ui.playerlist

import com.rmitsubayashi.pennantmanager.data.model.Position
import java.io.Serializable

data class FilterState(
    val positions: Set<Position>
) : Serializable