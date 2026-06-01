package com.holdthatbite.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class BiteCalendarTest {
    @Test
    fun monthGridUsesMondayStartAndSixRows() {
        val days = BiteCalendar.monthGrid(YearMonth.of(2026, 6))

        assertEquals(42, days.size)
        assertEquals(LocalDate.of(2026, 6, 1), days.first().date)
        assertEquals(LocalDate.of(2026, 7, 12), days.last().date)
        assertTrue(days.first().inCurrentPeriod)
        assertFalse(days.last().inCurrentPeriod)
    }

    @Test
    fun weekGridUsesSelectedWeekMondayToSunday() {
        val days = BiteCalendar.weekGrid(LocalDate.of(2026, 6, 4))

        assertEquals(7, days.size)
        assertEquals(LocalDate.of(2026, 6, 1), days.first().date)
        assertEquals(LocalDate.of(2026, 6, 7), days.last().date)
    }

    @Test
    fun dateKeyIsIsoLocalDate() {
        assertEquals("2026-06-01", BiteCalendar.dateKey(LocalDate.of(2026, 6, 1)))
    }
}

