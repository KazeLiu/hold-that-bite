package com.holdthatbite.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class WeightTrendTest {
    @Test
    fun trendEntriesAreSortedByTime() {
        val sorted = WeightTrend.sorted(
            listOf(
                WeightEntry(timestampMillis = 30L, weightKg = 61.2),
                WeightEntry(timestampMillis = 10L, weightKg = 62.0),
                WeightEntry(timestampMillis = 20L, weightKg = 61.6),
            )
        )

        assertEquals(listOf(10L, 20L, 30L), sorted.map { it.timestampMillis })
    }
}

