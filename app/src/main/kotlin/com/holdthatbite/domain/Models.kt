package com.holdthatbite.domain

enum class BiteStatus {
    KEPT,
    MISSED
}

enum class CalendarMode {
    MONTH,
    WEEK
}

enum class WeightUnit(val label: String, private val displayPerKg: Double) {
    KG(label = "kg", displayPerKg = 1.0),
    JIN(label = "斤", displayPerKg = 2.0);

    fun toDisplay(weightKg: Double): Double = weightKg * displayPerKg

    fun toKg(displayWeight: Double): Double = displayWeight / displayPerKg
}

data class AppSettings(
    val calendarMode: CalendarMode = CalendarMode.MONTH,
    val weightTrendEnabled: Boolean = false,
    val askWeightAfterCheckIn: Boolean = false,
    val targetWeightKg: Double? = null,
    val weightUnit: WeightUnit = WeightUnit.KG,
)

data class BiteRecord(
    val dateKey: String,
    val status: BiteStatus,
    val note: String = "",
)

data class WeightEntry(
    val timestampMillis: Long,
    val weightKg: Double,
)

data class CalendarDay(
    val date: java.time.LocalDate,
    val inCurrentPeriod: Boolean,
)
