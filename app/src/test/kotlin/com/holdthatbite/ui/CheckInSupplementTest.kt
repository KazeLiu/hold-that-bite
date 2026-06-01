package com.holdthatbite.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CheckInSupplementTest {
    @Test
    fun blankInputsDoNotProduceSupplementalData() {
        val supplement = CheckInSupplement.from(note = "  ", weight = " ")

        assertNull(supplement.note)
        assertNull(supplement.weightKg)
    }

    @Test
    fun filledInputsAreTrimmedAndParsed() {
        val supplement = CheckInSupplement.from(note = "  今天还行  ", weight = "62.5")

        assertEquals("今天还行", supplement.note)
        assertEquals(62.5, supplement.weightKg!!, 0.001)
    }
}

