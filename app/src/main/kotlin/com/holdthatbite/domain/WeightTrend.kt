package com.holdthatbite.domain

object WeightTrend {
    fun sorted(entries: List<WeightEntry>): List<WeightEntry> =
        entries.sortedBy { it.timestampMillis }
}

