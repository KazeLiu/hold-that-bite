package com.holdthatbite.data

import com.holdthatbite.domain.AppSettings
import com.holdthatbite.domain.BiteRecord
import com.holdthatbite.domain.BiteStatus
import com.holdthatbite.domain.CalendarMode
import com.holdthatbite.domain.FastingPlan
import com.holdthatbite.domain.ThemeMode
import com.holdthatbite.domain.WeightEntry
import com.holdthatbite.domain.WeightUnit
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BiteBackupCodecTest {
    @Test
    fun encodeWritesVersionedPortableJson() {
        val payload = samplePayload()

        val json = JSONObject(BiteBackupCodec.encode(payload))

        assertEquals(1, json.getInt("schemaVersion"))
        assertEquals(1780416000000L, json.getLong("exportedAtMillis"))
        assertEquals("WEEK", json.getJSONObject("settings").getString("calendarMode"))
        assertEquals("FOURTEEN_TEN", json.getJSONObject("settings").getString("fastingPlan"))
        assertTrue(json.getJSONObject("settings").getBoolean("onboardingGuideShown"))
        assertEquals("2026-06-02", json.getJSONArray("records").getJSONObject(0).getString("dateKey"))
        assertEquals(62.5, json.getJSONArray("weights").getJSONObject(0).getDouble("weightKg"), 0.0001)
        assertEquals(3, json.getJSONArray("snackRefusals").getJSONObject(0).getInt("count"))
    }

    @Test
    fun decodeRestoresValidBackupPayload() {
        val encoded = BiteBackupCodec.encode(samplePayload())

        val decoded = BiteBackupCodec.decode(encoded)

        assertEquals(1780416000000L, decoded.exportedAtMillis)
        assertEquals(CalendarMode.WEEK, decoded.settings.calendarMode)
        assertEquals(FastingPlan.FOURTEEN_TEN, decoded.settings.fastingPlan)
        assertEquals(10, decoded.settings.firstMealHour)
        assertEquals(30, decoded.settings.firstMealMinute)
        assertTrue(decoded.settings.weightTrendEnabled)
        assertFalse(decoded.settings.askWeightAfterCheckIn)
        assertEquals(61.2, decoded.settings.targetWeightKg ?: 0.0, 0.0001)
        assertEquals(WeightUnit.JIN, decoded.settings.weightUnit)
        assertEquals(ThemeMode.DARK, decoded.settings.themeMode)
        assertTrue(decoded.settings.privacyPolicyAccepted)
        assertFalse(decoded.settings.analyticsEnabled)
        assertTrue(decoded.settings.onboardingGuideShown)
        assertEquals(BiteRecord("2026-06-02", BiteStatus.KEPT, "守住"), decoded.records.single())
        assertEquals(WeightEntry(1780416000000L, 62.5), decoded.weights.single())
        assertEquals(3, decoded.snackRefusals["2026-06-02"])
    }

    @Test(expected = BiteBackupDecodeException::class)
    fun decodeRejectsUnsupportedSchemaVersion() {
        BiteBackupCodec.decode("""{"schemaVersion":99}""")
    }

    @Test
    fun decodeDefaultsInvalidOptionalSettingsAndFiltersInvalidRows() {
        val decoded = BiteBackupCodec.decode(
            """
            {
              "schemaVersion": 1,
              "exportedAtMillis": -10,
              "settings": {
                "calendarMode": "BROKEN",
                "fastingPlan": "BROKEN",
                "firstMealHour": 88,
                "firstMealMinute": -2,
                "weightTrendEnabled": true,
                "askWeightAfterCheckIn": true,
                "targetWeightKg": "not-a-number",
                "weightUnit": "BROKEN",
                "themeMode": "BROKEN",
                "privacyPolicyAccepted": true,
                "analyticsEnabled": true
              },
              "records": [
                { "dateKey": "", "status": "KEPT", "note": "bad" },
                { "dateKey": "2026-06-03", "status": "BROKEN", "note": "bad" },
                { "dateKey": "2026-06-04", "status": "MISSED", "note": "ok" }
              ],
              "weights": [
                { "timestampMillis": 0, "weightKg": 62.5 },
                { "timestampMillis": 1780502400000, "weightKg": "NaN" },
                { "timestampMillis": 1780588800000, "weightKg": 63.1 }
              ],
              "snackRefusals": [
                { "dateKey": "", "count": 2 },
                { "dateKey": "2026-06-02", "count": 0 },
                { "dateKey": "2026-06-03", "count": 2 }
              ]
            }
            """.trimIndent()
        )

        assertEquals(0L, decoded.exportedAtMillis)
        assertEquals(AppSettings().calendarMode, decoded.settings.calendarMode)
        assertEquals(AppSettings().fastingPlan, decoded.settings.fastingPlan)
        assertEquals(23, decoded.settings.firstMealHour)
        assertEquals(0, decoded.settings.firstMealMinute)
        assertTrue(decoded.settings.weightTrendEnabled)
        assertTrue(decoded.settings.askWeightAfterCheckIn)
        assertNull(decoded.settings.targetWeightKg)
        assertEquals(AppSettings().weightUnit, decoded.settings.weightUnit)
        assertEquals(AppSettings().themeMode, decoded.settings.themeMode)
        assertTrue(decoded.settings.privacyPolicyAccepted)
        assertTrue(decoded.settings.analyticsEnabled)
        assertFalse(decoded.settings.onboardingGuideShown)
        assertEquals(listOf(BiteRecord("2026-06-04", BiteStatus.MISSED, "ok")), decoded.records)
        assertEquals(listOf(WeightEntry(1780588800000L, 63.1)), decoded.weights)
        assertEquals(mapOf("2026-06-03" to 2), decoded.snackRefusals)
    }

    private fun samplePayload(): BiteBackupPayload {
        return BiteBackupPayload(
            exportedAtMillis = 1780416000000L,
            settings = AppSettings(
                calendarMode = CalendarMode.WEEK,
                fastingPlan = FastingPlan.FOURTEEN_TEN,
                firstMealHour = 10,
                firstMealMinute = 30,
                weightTrendEnabled = true,
                askWeightAfterCheckIn = false,
                targetWeightKg = 61.2,
                weightUnit = WeightUnit.JIN,
                themeMode = ThemeMode.DARK,
                privacyPolicyAccepted = true,
                analyticsEnabled = false,
                onboardingGuideShown = true,
            ),
            records = listOf(BiteRecord("2026-06-02", BiteStatus.KEPT, "守住")),
            weights = listOf(WeightEntry(1780416000000L, 62.5)),
            snackRefusals = mapOf("2026-06-02" to 3),
        )
    }
}
