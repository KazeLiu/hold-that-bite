package com.holdthatbite.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MotivationTest {
    @Test
    fun snackRefusalEncouragementUsesHighestReachedTier() {
        assertNull(Motivation.snackRefusalEncouragement(0))
        assertEquals(
            SnackRefusalEncouragement(
                shortLabel = "小胜利",
                detail = "第 1 次，先赢一口",
                intensity = 1,
            ),
            Motivation.snackRefusalEncouragement(1),
        )
        assertEquals("今天很稳", Motivation.snackRefusalEncouragement(4)?.shortLabel)
        assertEquals("防线拉满", Motivation.snackRefusalEncouragement(8)?.shortLabel)
        assertEquals("硬控住了", Motivation.snackRefusalEncouragement(12)?.shortLabel)
    }
}
