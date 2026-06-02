# Data Backup Import Export Design

## Goal

Add a settings-page backup feature that exports all local app data to a versioned JSON file and imports the same file with full local overwrite semantics.

## User Flow

The settings page adds a `数据备份` card with two actions:

- `导出数据`: opens Android's document creator and writes a JSON backup file named like `hold-that-bite-backup-20260602.json`.
- `导入数据`: opens Android's document picker, parses the selected JSON file, then asks for confirmation before overwriting current local data.

If import succeeds, the app reloads settings, check-in records, weight entries, and snack refusal counters immediately. If import fails, the app shows a short failure message and leaves current data unchanged.

## File Schema

The backup file is a UTF-8 JSON object:

```json
{
  "schemaVersion": 1,
  "exportedAtMillis": 1780416000000,
  "settings": {
    "calendarMode": "MONTH",
    "fastingPlan": "SIXTEEN_EIGHT",
    "firstMealHour": 9,
    "firstMealMinute": 0,
    "weightTrendEnabled": false,
    "askWeightAfterCheckIn": false,
    "targetWeightKg": null,
    "weightUnit": "KG",
    "themeMode": "SYSTEM",
    "privacyPolicyAccepted": true,
    "analyticsEnabled": false
  },
  "records": [
    { "dateKey": "2026-06-02", "status": "KEPT", "note": "example" }
  ],
  "weights": [
    { "timestampMillis": 1780416000000, "weightKg": 62.5 }
  ],
  "snackRefusals": [
    { "dateKey": "2026-06-02", "count": 3 }
  ]
}
```

Unknown future top-level fields are ignored. Unsupported `schemaVersion` values fail import. Invalid enum values, blank date keys, non-positive snack counts, non-positive timestamps, and invalid weights are skipped or defaulted the same way current store loading already handles malformed stored JSON.

## Architecture

Add a pure Kotlin `BiteBackupCodec` in `com.holdthatbite.data` to encode and decode backup JSON without Android framework dependencies. `BiteStore` remains the persistence boundary and gains `exportBackupPayload` plus `replaceAllFromBackup` methods. `MainActivity` only owns the Android document picker, stream reading/writing, user confirmation, and UI state refresh.

## Error Handling

Export catches stream and encoding errors and shows `导出失败，请稍后再试`. Import catches file read, parse, schema, and write errors and shows `导入失败，请确认文件是否正确`. Import confirmation is required because the operation overwrites all current local records.

## Testing

Use Level 2 TDD for the codec and overwrite semantics:

- Encode should include schema version, settings, records, weights, and snack refusal counts.
- Decode should reconstruct a payload from valid JSON.
- Decode should reject unsupported schema versions.
- Decode should tolerate invalid optional fields using current defaults and filters.

Android file picker behavior is covered by build verification and emulator smoke testing because it depends on system UI.
