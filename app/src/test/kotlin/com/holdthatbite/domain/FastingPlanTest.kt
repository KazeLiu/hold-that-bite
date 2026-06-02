package com.holdthatbite.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

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
}
