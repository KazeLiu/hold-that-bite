package com.holdthatbite.ui

import com.holdthatbite.domain.WeightEntry
import com.holdthatbite.domain.WeightTrend

sealed class WeightChartModel {
    data object Empty : WeightChartModel()

    data class Ready(
        val entries: List<WeightEntry>,
    ) : WeightChartModel()

    companion object {
        fun from(entries: List<WeightEntry>): WeightChartModel {
            val sorted = WeightTrend.sorted(entries)
            return if (sorted.isEmpty()) Empty else Ready(sorted)
        }
    }
}

