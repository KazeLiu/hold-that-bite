package com.holdthatbite.data

import android.content.Context
import com.holdthatbite.domain.AppSettings
import com.holdthatbite.domain.BiteRecord
import com.holdthatbite.domain.BiteStatus
import com.holdthatbite.domain.CalendarMode
import com.holdthatbite.domain.FastingPlan
import com.holdthatbite.domain.SnackRefusalCounter
import com.holdthatbite.domain.ThemeMode
import com.holdthatbite.domain.WeightEntry
import com.holdthatbite.domain.WeightUnit
import org.json.JSONArray
import org.json.JSONObject

class BiteStore(context: Context) {
    private val preferences = context.getSharedPreferences("hold_that_bite", Context.MODE_PRIVATE)

    /** Builds a complete backup snapshot of user-controlled local data. */
    fun exportBackupPayload(exportedAtMillis: Long = System.currentTimeMillis()): BiteBackupPayload {
        return BiteBackupPayload(
            exportedAtMillis = exportedAtMillis,
            settings = loadSettings(),
            records = loadRecords().values.sortedBy { it.dateKey },
            weights = loadWeights(),
            snackRefusals = loadSnackRefusals(),
        )
    }

    /** Replaces the whole local data set with a decoded backup payload. */
    fun replaceAllFromBackup(payload: BiteBackupPayload) {
        preferences.edit().clear().apply()
        saveSettings(payload.settings)
        saveRecords(payload.records.sortedBy { it.dateKey })
        saveWeights(payload.weights.sortedBy { it.timestampMillis })
        saveSnackRefusals(payload.snackRefusals)
    }

    fun loadSettings(): AppSettings {
        val mode = runCatching {
            CalendarMode.valueOf(preferences.getString(KEY_CALENDAR_MODE, CalendarMode.MONTH.name) ?: CalendarMode.MONTH.name)
        }.getOrDefault(CalendarMode.MONTH)
        val weightUnit = runCatching {
            WeightUnit.valueOf(preferences.getString(KEY_WEIGHT_UNIT, WeightUnit.KG.name) ?: WeightUnit.KG.name)
        }.getOrDefault(WeightUnit.KG)
        val fastingPlan = runCatching {
            FastingPlan.valueOf(
                preferences.getString(KEY_FASTING_PLAN, FastingPlan.SIXTEEN_EIGHT.name)
                    ?: FastingPlan.SIXTEEN_EIGHT.name
            )
        }.getOrDefault(FastingPlan.SIXTEEN_EIGHT)
        val themeMode = runCatching {
            ThemeMode.valueOf(preferences.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name)
        }.getOrDefault(ThemeMode.SYSTEM)

        return AppSettings(
            calendarMode = mode,
            fastingPlan = fastingPlan,
            firstMealHour = preferences.getInt(KEY_FIRST_MEAL_HOUR, 9).coerceIn(0, 23),
            firstMealMinute = preferences.getInt(KEY_FIRST_MEAL_MINUTE, 0).coerceIn(0, 59),
            weightTrendEnabled = preferences.getBoolean(KEY_WEIGHT_TREND_ENABLED, false),
            askWeightAfterCheckIn = preferences.getBoolean(KEY_ASK_WEIGHT_AFTER_CHECK_IN, false),
            targetWeightKg = preferences.getString(KEY_TARGET_WEIGHT_KG, null)?.toDoubleOrNull(),
            weightUnit = weightUnit,
            themeMode = themeMode,
            privacyPolicyAccepted = preferences.getBoolean(KEY_PRIVACY_POLICY_ACCEPTED, false),
            analyticsEnabled = preferences.getBoolean(KEY_ANALYTICS_ENABLED, false),
            onboardingGuideShown = preferences.getBoolean(KEY_ONBOARDING_GUIDE_SHOWN, false),
        )
    }

    fun saveSettings(settings: AppSettings) {
        val editor = preferences.edit()
            .putString(KEY_CALENDAR_MODE, settings.calendarMode.name)
            .putString(KEY_FASTING_PLAN, settings.fastingPlan.name)
            .putInt(KEY_FIRST_MEAL_HOUR, settings.firstMealHour.coerceIn(0, 23))
            .putInt(KEY_FIRST_MEAL_MINUTE, settings.firstMealMinute.coerceIn(0, 59))
            .putBoolean(KEY_WEIGHT_TREND_ENABLED, settings.weightTrendEnabled)
            .putBoolean(KEY_ASK_WEIGHT_AFTER_CHECK_IN, settings.askWeightAfterCheckIn)
            .putString(KEY_WEIGHT_UNIT, settings.weightUnit.name)
            .putString(KEY_THEME_MODE, settings.themeMode.name)
            .putBoolean(KEY_PRIVACY_POLICY_ACCEPTED, settings.privacyPolicyAccepted)
            .putBoolean(KEY_ANALYTICS_ENABLED, settings.analyticsEnabled)
            .putBoolean(KEY_ONBOARDING_GUIDE_SHOWN, settings.onboardingGuideShown)
        if (settings.targetWeightKg == null) {
            editor.remove(KEY_TARGET_WEIGHT_KG)
        } else {
            editor.putString(KEY_TARGET_WEIGHT_KG, settings.targetWeightKg.toString())
        }
        editor.apply()
    }

    fun loadRecords(): Map<String, BiteRecord> {
        val array = JSONArray(preferences.getString(KEY_RECORDS, "[]") ?: "[]")
        val records = linkedMapOf<String, BiteRecord>()

        for (index in 0 until array.length()) {
            val item = array.optJSONObject(index) ?: continue
            val dateKey = item.optString("dateKey")
            val status = runCatching { BiteStatus.valueOf(item.optString("status")) }.getOrNull() ?: continue
            records[dateKey] = BiteRecord(
                dateKey = dateKey,
                status = status,
                note = item.optString("note", ""),
            )
        }

        return records
    }

    fun upsertRecord(record: BiteRecord) {
        val records = loadRecords().toMutableMap()
        records[record.dateKey] = record
        saveRecords(records.values.sortedBy { it.dateKey })
    }

    fun loadWeights(): List<WeightEntry> {
        val array = JSONArray(preferences.getString(KEY_WEIGHTS, "[]") ?: "[]")
        val entries = mutableListOf<WeightEntry>()

        for (index in 0 until array.length()) {
            val item = array.optJSONObject(index) ?: continue
            val timestamp = item.optLong("timestampMillis", -1L)
            val weight = item.optDouble("weightKg", Double.NaN)
            if (timestamp > 0L && !weight.isNaN()) {
                entries += WeightEntry(timestamp, weight)
            }
        }

        return entries.sortedBy { it.timestampMillis }
    }

    fun addWeight(entry: WeightEntry) {
        saveWeights((loadWeights() + entry).sortedBy { it.timestampMillis })
    }

    fun deleteWeight(timestampMillis: Long) {
        saveWeights(loadWeights().filterNot { it.timestampMillis == timestampMillis })
    }

    fun loadSnackRefusals(): Map<String, Int> {
        val array = JSONArray(preferences.getString(KEY_SNACK_REFUSALS, "[]") ?: "[]")
        val counts = linkedMapOf<String, Int>()

        for (index in 0 until array.length()) {
            val item = array.optJSONObject(index) ?: continue
            val dateKey = item.optString("dateKey")
            val count = item.optInt("count", 0)
            if (dateKey.isNotBlank() && count > 0) {
                counts[dateKey] = count
            }
        }

        return counts
    }

    fun incrementSnackRefusal(dateKey: String) {
        val counts = loadSnackRefusals().toMutableMap()
        counts[dateKey] = SnackRefusalCounter.increment(counts[dateKey] ?: 0)
        saveSnackRefusals(counts)
    }

    fun undoSnackRefusal(dateKey: String) {
        val counts = loadSnackRefusals().toMutableMap()
        val nextCount = SnackRefusalCounter.decrement(counts[dateKey] ?: 0)
        if (nextCount == 0) {
            counts.remove(dateKey)
        } else {
            counts[dateKey] = nextCount
        }
        saveSnackRefusals(counts)
    }

    private fun saveRecords(records: List<BiteRecord>) {
        val array = JSONArray()
        records.forEach { record ->
            array.put(
                JSONObject()
                    .put("dateKey", record.dateKey)
                    .put("status", record.status.name)
                    .put("note", record.note)
            )
        }
        preferences.edit().putString(KEY_RECORDS, array.toString()).apply()
    }

    private fun saveWeights(entries: List<WeightEntry>) {
        val array = JSONArray()
        entries.forEach { entry ->
            array.put(
                JSONObject()
                    .put("timestampMillis", entry.timestampMillis)
                    .put("weightKg", entry.weightKg)
            )
        }
        preferences.edit().putString(KEY_WEIGHTS, array.toString()).apply()
    }

    private fun saveSnackRefusals(counts: Map<String, Int>) {
        val array = JSONArray()
        counts.toSortedMap().forEach { (dateKey, count) ->
            if (count > 0) {
                array.put(
                    JSONObject()
                        .put("dateKey", dateKey)
                        .put("count", count)
                )
            }
        }
        preferences.edit().putString(KEY_SNACK_REFUSALS, array.toString()).apply()
    }

    private companion object {
        const val KEY_CALENDAR_MODE = "calendar_mode"
        const val KEY_FASTING_PLAN = "fasting_plan"
        const val KEY_FIRST_MEAL_HOUR = "first_meal_hour"
        const val KEY_FIRST_MEAL_MINUTE = "first_meal_minute"
        const val KEY_WEIGHT_TREND_ENABLED = "weight_trend_enabled"
        const val KEY_ASK_WEIGHT_AFTER_CHECK_IN = "ask_weight_after_check_in"
        const val KEY_TARGET_WEIGHT_KG = "target_weight_kg"
        const val KEY_WEIGHT_UNIT = "weight_unit"
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_PRIVACY_POLICY_ACCEPTED = "privacy_policy_accepted"
        const val KEY_ANALYTICS_ENABLED = "analytics_enabled"
        const val KEY_ONBOARDING_GUIDE_SHOWN = "onboarding_guide_shown"
        const val KEY_RECORDS = "records"
        const val KEY_WEIGHTS = "weights"
        const val KEY_SNACK_REFUSALS = "snack_refusals"
    }
}
