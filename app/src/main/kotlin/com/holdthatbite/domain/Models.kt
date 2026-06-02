package com.holdthatbite.domain

enum class BiteStatus {
    KEPT,
    MISSED
}

enum class CalendarMode {
    MONTH,
    WEEK
}

enum class ThemeMode(val label: String) {
    SYSTEM("跟随手机"),
    LIGHT("浅色"),
    DARK("夜间");

    fun shouldUseDarkTheme(systemInDarkTheme: Boolean): Boolean {
        return when (this) {
            SYSTEM -> systemInDarkTheme
            LIGHT -> false
            DARK -> true
        }
    }
}

enum class WeightUnit(val label: String, private val displayPerKg: Double) {
    KG(label = "kg", displayPerKg = 1.0),
    JIN(label = "斤", displayPerKg = 2.0);

    fun toDisplay(weightKg: Double): Double = weightKg * displayPerKg

    fun toKg(displayWeight: Double): Double = displayWeight / displayPerKg
}

data class AppSettings(
    val calendarMode: CalendarMode = CalendarMode.MONTH,
    val fastingPlan: FastingPlan = FastingPlan.SIXTEEN_EIGHT,
    val firstMealHour: Int = 9,
    val firstMealMinute: Int = 0,
    val weightTrendEnabled: Boolean = false,
    val askWeightAfterCheckIn: Boolean = false,
    val targetWeightKg: Double? = null,
    val weightUnit: WeightUnit = WeightUnit.KG,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val privacyPolicyAccepted: Boolean = false,
    val analyticsEnabled: Boolean = false,
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
