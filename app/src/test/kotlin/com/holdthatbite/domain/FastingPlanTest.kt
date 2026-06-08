package com.holdthatbite.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class FastingPlanTest {
    @Test
    fun defaultSettingsUseSixteenEightAndNineAmFirstMeal() {
        val settings = AppSettings()

        assertEquals(FastingPlan.SIXTEEN_EIGHT, settings.fastingPlan)
        assertEquals(9, settings.firstMealHour)
        assertEquals(0, settings.firstMealMinute)
    }

    @Test
    fun plansExplainEatingAndFastingWindows() {
        assertEquals("16+8", FastingPlan.SIXTEEN_EIGHT.label)
        assertEquals("8个小时吃东西，16个小时不吃东西", FastingPlan.SIXTEEN_EIGHT.description)
        assertEquals("10个小时吃东西，14个小时不吃东西", FastingPlan.FOURTEEN_TEN.description)
    }

    @Test
    fun plansAreSortedFromLongestToShortestEatingWindow() {
        assertEquals(
            listOf("12+12", "14+10", "16+8", "18+6", "20+4"),
            FastingPlan.values().map { it.label },
        )
    }

    @Test
    fun lastBiteTimeUsesEatingWindow() {
        val result = FastingPlan.SIXTEEN_EIGHT.lastBiteTime(MealTime(hour = 9, minute = 0))

        assertEquals(17, result.hour)
        assertEquals(0, result.minute)
        assertFalse(result.isNextDay)
        assertEquals("17:00", result.displayText)
    }

    @Test
    fun lastBiteTimeMarksNextDayWhenCrossingMidnight() {
        val result = FastingPlan.TWENTY_FOUR.lastBiteTime(MealTime(hour = 23, minute = 30))

        assertEquals(3, result.hour)
        assertEquals(30, result.minute)
        assertTrue(result.isNextDay)
        assertEquals("次日 03:30", result.displayText)
    }

    @Test
    fun eatingWindowIncludesFirstMealTimeAndBeforeLastBite() {
        val firstMeal = MealTime(hour = 9, minute = 0)

        assertTrue(FastingPlan.SIXTEEN_EIGHT.isEatingWindow(firstMeal, MealTime(hour = 9, minute = 0)))
        assertTrue(FastingPlan.SIXTEEN_EIGHT.isEatingWindow(firstMeal, MealTime(hour = 16, minute = 59)))
        assertFalse(FastingPlan.SIXTEEN_EIGHT.isEatingWindow(firstMeal, MealTime(hour = 17, minute = 0)))
    }

    @Test
    fun fastingWindowIsOutsideEatingWindow() {
        val firstMeal = MealTime(hour = 9, minute = 0)

        assertFalse(FastingPlan.SIXTEEN_EIGHT.isEatingWindow(firstMeal, MealTime(hour = 8, minute = 59)))
        assertFalse(FastingPlan.SIXTEEN_EIGHT.isEatingWindow(firstMeal, MealTime(hour = 23, minute = 0)))
    }

    @Test
    fun crossMidnightEatingWindowStaysOpenAfterMidnight() {
        val firstMeal = MealTime(hour = 23, minute = 0)

        assertTrue(FastingPlan.TWENTY_FOUR.isEatingWindow(firstMeal, MealTime(hour = 23, minute = 30)))
        assertTrue(FastingPlan.TWENTY_FOUR.isEatingWindow(firstMeal, MealTime(hour = 2, minute = 59)))
        assertFalse(FastingPlan.TWENTY_FOUR.isEatingWindow(firstMeal, MealTime(hour = 3, minute = 0)))
    }

    @Test
    fun dayPhaseSplitsNaturalDayAroundFirstMealAndLastBite() {
        val firstMeal = MealTime(hour = 9, minute = 0)

        assertEquals(
            FastingDayPhase.BEFORE_FIRST_MEAL,
            FastingPlan.SIXTEEN_EIGHT.dayPhase(firstMeal, MealTime(hour = 8, minute = 59)),
        )
        assertEquals(
            FastingDayPhase.EATING_WINDOW,
            FastingPlan.SIXTEEN_EIGHT.dayPhase(firstMeal, MealTime(hour = 9, minute = 0)),
        )
        assertEquals(
            FastingDayPhase.EATING_WINDOW,
            FastingPlan.SIXTEEN_EIGHT.dayPhase(firstMeal, MealTime(hour = 16, minute = 59)),
        )
        assertEquals(
            FastingDayPhase.AFTER_LAST_BITE,
            FastingPlan.SIXTEEN_EIGHT.dayPhase(firstMeal, MealTime(hour = 17, minute = 0)),
        )
    }

    @Test
    fun latestFirstMealKeepsEatingWindowInsideNaturalDay() {
        assertEquals(MealTime(hour = 16, minute = 0), FastingPlan.SIXTEEN_EIGHT.latestSameDayFirstMeal())
        assertTrue(FastingPlan.SIXTEEN_EIGHT.isSameDayPlan(MealTime(hour = 16, minute = 0)))
        assertFalse(FastingPlan.SIXTEEN_EIGHT.isSameDayPlan(MealTime(hour = 16, minute = 1)))
    }

    @Test
    fun dailyFirstMealOverrideOnlyAppliesToMatchingDate() {
        val defaultFirstMeal = MealTime(hour = 9, minute = 0)
        val override = DailyFirstMealOverride(
            date = LocalDate.of(2026, 6, 4),
            firstMeal = MealTime(hour = 7, minute = 0),
        )

        assertEquals(
            MealTime(hour = 7, minute = 0),
            override.firstMealFor(LocalDate.of(2026, 6, 4), defaultFirstMeal),
        )
        assertEquals(
            defaultFirstMeal,
            override.firstMealFor(LocalDate.of(2026, 6, 5), defaultFirstMeal),
        )
    }
}
