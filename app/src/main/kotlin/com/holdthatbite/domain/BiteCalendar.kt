package com.holdthatbite.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

object BiteCalendar {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun dateKey(date: LocalDate): String = date.format(formatter)

    fun monthGrid(month: YearMonth): List<CalendarDay> {
        val first = month.atDay(1)
        val start = first.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return (0 until 42).map { offset ->
            val date = start.plusDays(offset.toLong())
            CalendarDay(date, YearMonth.from(date) == month)
        }
    }

    fun weekGrid(selectedDate: LocalDate): List<CalendarDay> {
        val start = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return (0 until 7).map { offset ->
            CalendarDay(start.plusDays(offset.toLong()), true)
        }
    }
}

