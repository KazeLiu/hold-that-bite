package com.holdthatbite.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class SnackRefusalCounterTest {
    @Test
    fun incrementAddsOneSmallVictory() {
        assertEquals(1, SnackRefusalCounter.increment(0))
        assertEquals(4, SnackRefusalCounter.increment(3))
    }

    @Test
    fun decrementNeverGoesBelowZero() {
        assertEquals(2, SnackRefusalCounter.decrement(3))
        assertEquals(0, SnackRefusalCounter.decrement(0))
    }
}
