package com.holdthatbite.data

import com.holdthatbite.domain.AppSettings
import com.holdthatbite.domain.BiteRecord
import com.holdthatbite.domain.BiteStatus
import com.holdthatbite.domain.CalendarMode
import com.holdthatbite.domain.FastingPlan
import com.holdthatbite.domain.ThemeMode
import com.holdthatbite.domain.WeightEntry
import com.holdthatbite.domain.WeightUnit
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

data class BiteBackupPayload(
    val exportedAtMillis: Long,
    val settings: AppSettings,
    val records: List<BiteRecord>,
    val weights: List<WeightEntry>,
    val snackRefusals: Map<String, Int>,
)

class BiteBackupDecodeException(message: String, cause: Throwable? = null) : Exception(message, cause)

object BiteBackupCodec {
    const val SCHEMA_VERSION = 1

    /** Encodes all user-controlled local data into a stable JSON schema. */
    fun encode(payload: BiteBackupPayload): String {
        return JSONObject()
            .put("schemaVersion", SCHEMA_VERSION)
            .put("exportedAtMillis", payload.exportedAtMillis.coerceAtLeast(0L))
            .put("settings", encodeSettings(payload.settings))
            .put("records", encodeRecords(payload.records))
            .put("weights", encodeWeights(payload.weights))
            .put("snackRefusals", encodeSnackRefusals(payload.snackRefusals))
            .toString()
    }

    /** Decodes a backup file and validates the schema version before it can overwrite storage. */
    fun decode(json: String): BiteBackupPayload {
        val root = try {
            JSONObject(json)
        } catch (error: JSONException) {
            throw BiteBackupDecodeException("Backup file is not valid JSON.", error)
        }

        val schemaVersion = root.optInt("schemaVersion", -1)
        if (schemaVersion != SCHEMA_VERSION) {
            throw BiteBackupDecodeException("Unsupported backup schema version: $schemaVersion.")
        }

        return BiteBackupPayload(
            exportedAtMillis = root.optLong("exportedAtMillis", 0L).coerceAtLeast(0L),
            settings = decodeSettings(root.optJSONObject("settings") ?: JSONObject()),
            records = decodeRecords(root.optJSONArray("records") ?: JSONArray()),
            weights = decodeWeights(root.optJSONArray("weights") ?: JSONArray()),
            snackRefusals = decodeSnackRefusals(root.optJSONArray("snackRefusals") ?: JSONArray()),
        )
    }

    private fun encodeSettings(settings: AppSettings): JSONObject {
        return JSONObject()
            .put("calendarMode", settings.calendarMode.name)
            .put("fastingPlan", settings.fastingPlan.name)
            .put("firstMealHour", settings.firstMealHour.coerceIn(0, 23))
            .put("firstMealMinute", settings.firstMealMinute.coerceIn(0, 59))
            .put("weightTrendEnabled", settings.weightTrendEnabled)
            .put("askWeightAfterCheckIn", settings.askWeightAfterCheckIn)
            .put("targetWeightKg", settings.targetWeightKg ?: JSONObject.NULL)
            .put("weightUnit", settings.weightUnit.name)
            .put("themeMode", settings.themeMode.name)
            .put("privacyPolicyAccepted", settings.privacyPolicyAccepted)
            .put("analyticsEnabled", settings.analyticsEnabled)
    }

    private fun decodeSettings(json: JSONObject): AppSettings {
        val defaults = AppSettings()
        return AppSettings(
            calendarMode = decodeEnum(json.optString("calendarMode"), defaults.calendarMode),
            fastingPlan = decodeEnum(json.optString("fastingPlan"), defaults.fastingPlan),
            firstMealHour = json.optInt("firstMealHour", defaults.firstMealHour).coerceIn(0, 23),
            firstMealMinute = json.optInt("firstMealMinute", defaults.firstMealMinute).coerceIn(0, 59),
            weightTrendEnabled = json.optBoolean("weightTrendEnabled", defaults.weightTrendEnabled),
            askWeightAfterCheckIn = json.optBoolean("askWeightAfterCheckIn", defaults.askWeightAfterCheckIn),
            targetWeightKg = decodeNullableDouble(json, "targetWeightKg"),
            weightUnit = decodeEnum(json.optString("weightUnit"), defaults.weightUnit),
            themeMode = decodeEnum(json.optString("themeMode"), defaults.themeMode),
            privacyPolicyAccepted = json.optBoolean("privacyPolicyAccepted", defaults.privacyPolicyAccepted),
            analyticsEnabled = json.optBoolean("analyticsEnabled", defaults.analyticsEnabled),
        )
    }

    private fun encodeRecords(records: List<BiteRecord>): JSONArray {
        val array = JSONArray()
        records.sortedBy { it.dateKey }.forEach { record ->
            if (record.dateKey.isNotBlank()) {
                array.put(
                    JSONObject()
                        .put("dateKey", record.dateKey)
                        .put("status", record.status.name)
                        .put("note", record.note)
                )
            }
        }
        return array
    }

    private fun decodeRecords(array: JSONArray): List<BiteRecord> {
        val records = mutableListOf<BiteRecord>()
        for (index in 0 until array.length()) {
            val item = array.optJSONObject(index) ?: continue
            val dateKey = item.optString("dateKey")
            val status = runCatching { BiteStatus.valueOf(item.optString("status")) }.getOrNull()
            if (dateKey.isNotBlank() && status != null) {
                records += BiteRecord(
                    dateKey = dateKey,
                    status = status,
                    note = item.optString("note", ""),
                )
            }
        }
        return records.sortedBy { it.dateKey }
    }

    private fun encodeWeights(weights: List<WeightEntry>): JSONArray {
        val array = JSONArray()
        weights.sortedBy { it.timestampMillis }.forEach { entry ->
            if (entry.timestampMillis > 0L && entry.weightKg.isFinite()) {
                array.put(
                    JSONObject()
                        .put("timestampMillis", entry.timestampMillis)
                        .put("weightKg", entry.weightKg)
                )
            }
        }
        return array
    }

    private fun decodeWeights(array: JSONArray): List<WeightEntry> {
        val weights = mutableListOf<WeightEntry>()
        for (index in 0 until array.length()) {
            val item = array.optJSONObject(index) ?: continue
            val timestamp = item.optLong("timestampMillis", -1L)
            val weight = decodeDouble(item, "weightKg") ?: continue
            if (timestamp > 0L && weight > 0.0 && weight < MAX_REASONABLE_WEIGHT_KG) {
                weights += WeightEntry(timestamp, weight)
            }
        }
        return weights.sortedBy { it.timestampMillis }
    }

    private fun encodeSnackRefusals(counts: Map<String, Int>): JSONArray {
        val array = JSONArray()
        counts.toSortedMap().forEach { (dateKey, count) ->
            if (dateKey.isNotBlank() && count > 0) {
                array.put(
                    JSONObject()
                        .put("dateKey", dateKey)
                        .put("count", count)
                )
            }
        }
        return array
    }

    private fun decodeSnackRefusals(array: JSONArray): Map<String, Int> {
        val counts = linkedMapOf<String, Int>()
        for (index in 0 until array.length()) {
            val item = array.optJSONObject(index) ?: continue
            val dateKey = item.optString("dateKey")
            val count = item.optInt("count", 0)
            if (dateKey.isNotBlank() && count > 0) {
                counts[dateKey] = count
            }
        }
        return counts.toSortedMap()
    }

    private inline fun <reified T : Enum<T>> decodeEnum(value: String, defaultValue: T): T {
        return runCatching { enumValueOf<T>(value) }.getOrDefault(defaultValue)
    }

    private fun decodeNullableDouble(json: JSONObject, key: String): Double? {
        if (!json.has(key) || json.isNull(key)) {
            return null
        }
        return decodeDouble(json, key)
    }

    private fun decodeDouble(json: JSONObject, key: String): Double? {
        val value = json.opt(key) ?: return null
        val parsed = when (value) {
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull()
            else -> null
        }
        return parsed?.takeIf { it.isFinite() }
    }

    private const val MAX_REASONABLE_WEIGHT_KG = 500.0
}
