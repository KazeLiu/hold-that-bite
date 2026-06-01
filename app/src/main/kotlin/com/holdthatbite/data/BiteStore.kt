package com.holdthatbite.data

import android.content.Context
import com.holdthatbite.domain.AppSettings
import com.holdthatbite.domain.BiteRecord
import com.holdthatbite.domain.BiteStatus
import com.holdthatbite.domain.CalendarMode
import com.holdthatbite.domain.WeightEntry
import com.holdthatbite.domain.WeightUnit
import org.json.JSONArray
import org.json.JSONObject

class BiteStore(context: Context) {
    private val preferences = context.getSharedPreferences("hold_that_bite", Context.MODE_PRIVATE)

    fun loadSettings(): AppSettings {
        val mode = runCatching {
            CalendarMode.valueOf(preferences.getString(KEY_CALENDAR_MODE, CalendarMode.MONTH.name) ?: CalendarMode.MONTH.name)
        }.getOrDefault(CalendarMode.MONTH)
        val weightUnit = runCatching {
            WeightUnit.valueOf(preferences.getString(KEY_WEIGHT_UNIT, WeightUnit.KG.name) ?: WeightUnit.KG.name)
        }.getOrDefault(WeightUnit.KG)

        return AppSettings(
            calendarMode = mode,
            weightTrendEnabled = preferences.getBoolean(KEY_WEIGHT_TREND_ENABLED, false),
            askWeightAfterCheckIn = preferences.getBoolean(KEY_ASK_WEIGHT_AFTER_CHECK_IN, false),
            targetWeightKg = preferences.getString(KEY_TARGET_WEIGHT_KG, null)?.toDoubleOrNull(),
            weightUnit = weightUnit,
        )
    }

    fun saveSettings(settings: AppSettings) {
        val editor = preferences.edit()
            .putString(KEY_CALENDAR_MODE, settings.calendarMode.name)
            .putBoolean(KEY_WEIGHT_TREND_ENABLED, settings.weightTrendEnabled)
            .putBoolean(KEY_ASK_WEIGHT_AFTER_CHECK_IN, settings.askWeightAfterCheckIn)
            .putString(KEY_WEIGHT_UNIT, settings.weightUnit.name)
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

    private companion object {
        const val KEY_CALENDAR_MODE = "calendar_mode"
        const val KEY_WEIGHT_TREND_ENABLED = "weight_trend_enabled"
        const val KEY_ASK_WEIGHT_AFTER_CHECK_IN = "ask_weight_after_check_in"
        const val KEY_TARGET_WEIGHT_KG = "target_weight_kg"
        const val KEY_WEIGHT_UNIT = "weight_unit"
        const val KEY_RECORDS = "records"
        const val KEY_WEIGHTS = "weights"
    }
}
