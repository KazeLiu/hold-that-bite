package com.holdthatbite.domain

import java.time.LocalDate

enum class FastingPlan(
    val label: String,
    val fastingHours: Int,
    val eatingHours: Int,
) {
    TWELVE_TWELVE(label = "12+12", fastingHours = 12, eatingHours = 12),
    FOURTEEN_TEN(label = "14+10", fastingHours = 14, eatingHours = 10),
    SIXTEEN_EIGHT(label = "16+8", fastingHours = 16, eatingHours = 8),
    EIGHTEEN_SIX(label = "18+6", fastingHours = 18, eatingHours = 6),
    TWENTY_FOUR(label = "20+4", fastingHours = 20, eatingHours = 4);

    val description: String
        get() = "${eatingHours}个小时吃东西，${fastingHours}个小时不吃东西"

    fun lastBiteTime(firstMeal: MealTime): LastBiteTime {
        val startMinutes = firstMeal.hour * MINUTES_PER_HOUR + firstMeal.minute
        val totalMinutes = startMinutes + eatingHours * MINUTES_PER_HOUR
        val dayMinutes = totalMinutes % MINUTES_PER_DAY
        return LastBiteTime(
            hour = dayMinutes / MINUTES_PER_HOUR,
            minute = dayMinutes % MINUTES_PER_HOUR,
            isNextDay = totalMinutes >= MINUTES_PER_DAY,
        )
    }

    fun latestSameDayFirstMeal(): MealTime {
        val latestMinutes = MINUTES_PER_DAY - eatingHours * MINUTES_PER_HOUR
        return MealTime(
            hour = latestMinutes / MINUTES_PER_HOUR,
            minute = latestMinutes % MINUTES_PER_HOUR,
        )
    }

    fun isSameDayPlan(firstMeal: MealTime): Boolean {
        return !lastBiteTime(firstMeal).isNextDay
    }

    fun dayPhase(firstMeal: MealTime, currentTime: MealTime): FastingDayPhase {
        val startMinutes = firstMeal.totalMinutes
        val currentMinutes = currentTime.totalMinutes
        val endMinutes = startMinutes + eatingHours * MINUTES_PER_HOUR

        return when {
            currentMinutes < startMinutes -> FastingDayPhase.BEFORE_FIRST_MEAL
            currentMinutes < endMinutes && endMinutes <= MINUTES_PER_DAY -> FastingDayPhase.EATING_WINDOW
            else -> FastingDayPhase.AFTER_LAST_BITE
        }
    }

    fun isEatingWindow(firstMeal: MealTime, currentTime: MealTime): Boolean {
        val startMinutes = firstMeal.totalMinutes
        val currentMinutes = currentTime.totalMinutes
        val endMinutes = (startMinutes + eatingHours * MINUTES_PER_HOUR) % MINUTES_PER_DAY

        return if (startMinutes < endMinutes) {
            currentMinutes in startMinutes until endMinutes
        } else {
            currentMinutes >= startMinutes || currentMinutes < endMinutes
        }
    }

    private companion object {
        const val MINUTES_PER_HOUR = 60
        const val MINUTES_PER_DAY = 24 * MINUTES_PER_HOUR
    }
}

enum class FastingDayPhase {
    BEFORE_FIRST_MEAL,
    EATING_WINDOW,
    AFTER_LAST_BITE
}

data class MealTime(
    val hour: Int,
    val minute: Int,
) {
    init {
        require(hour in 0..23) { "hour must be in 0..23" }
        require(minute in 0..59) { "minute must be in 0..59" }
    }

    val displayText: String
        get() = "%02d:%02d".format(hour, minute)

    internal val totalMinutes: Int
        get() = hour * 60 + minute
}

data class DailyFirstMealOverride(
    val date: LocalDate,
    val firstMeal: MealTime,
) {
    fun firstMealFor(targetDate: LocalDate, defaultFirstMeal: MealTime): MealTime {
        return if (targetDate == date) firstMeal else defaultFirstMeal
    }
}

data class LastBiteTime(
    val hour: Int,
    val minute: Int,
    val isNextDay: Boolean,
) {
    val displayText: String
        get() {
            val time = "%02d:%02d".format(hour, minute)
            return if (isNextDay) "次日 $time" else time
        }
}
