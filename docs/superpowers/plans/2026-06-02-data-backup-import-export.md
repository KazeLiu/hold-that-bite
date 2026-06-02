# Data Backup Import Export Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add versioned JSON backup export and full-overwrite import for all local Hold That Bite data.

**Architecture:** Keep storage in `BiteStore`, add a pure `BiteBackupCodec` for a portable JSON schema, and wire Android document picker actions from `SettingsPage`. Import parses first, confirms overwrite, writes all data, then reloads app state.

**Tech Stack:** Kotlin, Android SharedPreferences, `org.json`, Jetpack Compose, Android Activity Result document contracts, JUnit 4.

---

## File Structure

- Create `app/src/main/kotlin/com/holdthatbite/data/BiteBackupCodec.kt`: pure JSON encode/decode and backup payload model.
- Modify `app/src/main/kotlin/com/holdthatbite/data/BiteStore.kt`: export current store as payload and replace all local data from a payload.
- Modify `app/src/main/kotlin/com/holdthatbite/MainActivity.kt`: document picker launchers, import confirmation dialog, snackbar-style message state, settings-page backup buttons, and post-import refresh.
- Create `app/src/test/kotlin/com/holdthatbite/data/BiteBackupCodecTest.kt`: schema, round-trip, unsupported version, and malformed-field tests.

### Task 1: Backup Codec Tests

**Files:**
- Create: `app/src/test/kotlin/com/holdthatbite/data/BiteBackupCodecTest.kt`

- [ ] **Step 1: Write failing codec tests**

Add tests that call `BiteBackupCodec.encode` and `BiteBackupCodec.decode` with `BiteBackupPayload`, `AppSettings`, `BiteRecord`, `WeightEntry`, and snack refusal maps.

- [ ] **Step 2: Run test and verify RED**

Run: `.\gradlew.bat --no-daemon --max-workers=1 testDebugUnitTest --tests com.holdthatbite.data.BiteBackupCodecTest`

Expected: compile failure because `BiteBackupCodec` and `BiteBackupPayload` do not exist yet.

### Task 2: Backup Codec Implementation

**Files:**
- Create: `app/src/main/kotlin/com/holdthatbite/data/BiteBackupCodec.kt`
- Test: `app/src/test/kotlin/com/holdthatbite/data/BiteBackupCodecTest.kt`

- [ ] **Step 1: Implement payload and codec**

Create `BiteBackupPayload`, `BiteBackupDecodeException`, and `BiteBackupCodec` with schema version `1`, `encode(payload)`, and `decode(json)`.

- [ ] **Step 2: Run codec tests and verify GREEN**

Run: `.\gradlew.bat --no-daemon --max-workers=1 testDebugUnitTest --tests com.holdthatbite.data.BiteBackupCodecTest`

Expected: test report for `BiteBackupCodecTest` shows 0 failures. The local Gradle worker may still return non-zero due the existing worker startup environment issue; inspect XML when needed.

### Task 3: Store Export And Replace

**Files:**
- Modify: `app/src/main/kotlin/com/holdthatbite/data/BiteStore.kt`

- [ ] **Step 1: Add store methods**

Add `exportBackupPayload(exportedAtMillis: Long)` and `replaceAllFromBackup(payload: BiteBackupPayload)`. The replace method writes settings, sorted records, sorted weights, and sorted snack refusal counts.

- [ ] **Step 2: Compile**

Run: `.\gradlew.bat assembleDebug`

Expected: Kotlin compiles.

### Task 4: Settings UI And Document Picker

**Files:**
- Modify: `app/src/main/kotlin/com/holdthatbite/MainActivity.kt`

- [ ] **Step 1: Add Activity Result launchers**

Use `CreateDocument("application/json")` for export and `OpenDocument()` for import.

- [ ] **Step 2: Add import confirmation**

Store decoded payload in state, show an `AlertDialog`, and call `replaceAllFromBackup` only after confirmation.

- [ ] **Step 3: Add settings backup card**

Add `数据备份` with `导出数据` and `导入数据` buttons. Keep colors on existing tokens.

- [ ] **Step 4: Refresh state after import**

Reload settings, records, snack refusals, and weights after successful replace.

### Task 5: Verification And Install

**Files:**
- Verify all changed files.

- [ ] **Step 1: Run targeted tests**

Run codec test and inspect the XML report for failures.

- [ ] **Step 2: Build debug APK**

Run: `.\gradlew.bat assembleDebug`

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Install to MuMu if available**

Check `adb devices`; if a MuMu emulator/device is connected, install `app/build/outputs/apk/debug/app-debug.apk`.
