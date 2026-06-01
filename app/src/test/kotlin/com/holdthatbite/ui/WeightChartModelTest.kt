package com.holdthatbite.ui

import com.holdthatbite.domain.WeightEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeightChartModelTest {
    @Test
    fun emptyEntriesUseEmptyChartState() {
        val model = WeightChartModel.from(emptyList())

        assertTrue(model is WeightChartModel.Empty)
    }

    @Test
    fun entriesAreSortedBeforeChartRendering() {
        val model = WeightChartModel.from(
            listOf(
                WeightEntry(timestampMillis = 30L, weightKg = 61.2),
                WeightEntry(timestampMillis = 10L, weightKg = 62.0),
                WeightEntry(timestampMillis = 20L, weightKg = 61.6),
            )
        )

        require(model is WeightChartModel.Ready)
        assertEquals(listOf(10L, 20L, 30L), model.entries.map { it.timestampMillis })
    }
}

