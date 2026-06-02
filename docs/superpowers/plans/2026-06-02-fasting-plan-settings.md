# Fasting Plan Settings Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add configurable time-restricted eating plans to the settings page and calculate the last-bite time from the first-meal time.

**Architecture:** Add a focused domain model for fasting plans and schedule calculation, persist the selected values in `AppSettings` through `BiteStore`, then render a compact settings card in `MainActivity`. Keep UI labels derived from the domain model so stored values and displayed explanations stay consistent.

**Tech Stack:** Android, Kotlin, Jetpack Compose, Material 3, JUnit 4.

---

### Task 1: Domain Model And Tests

**Files:**
- Create: `app/src/main/kotlin/com/holdthatbite/domain/FastingPlan.kt`
- Test: `app/src/test/kotlin/com/holdthatbite/domain/FastingPlanTest.kt`

- [x] Add failing tests for default plan, plan explanations, normal calculation, and cross-midnight calculation.
- [x] Run `.\gradlew.bat testDebugUnitTest --tests com.holdthatbite.domain.FastingPlanTest` and confirm the new tests fail because the model does not exist.
- [x] Implement `FastingPlan`, `MealTime`, and `LastBiteTime`.
- [x] Re-run the focused unit test and confirm it passes.

### Task 2: Persist Settings

**Files:**
- Modify: `app/src/main/kotlin/com/holdthatbite/domain/Models.kt`
- Modify: `app/src/main/kotlin/com/holdthatbite/data/BiteStore.kt`

- [x] Add `fastingPlan`, `firstMealHour`, and `firstMealMinute` to `AppSettings`, defaulting to `SIXTEEN_EIGHT`, `9`, and `0`.
- [x] Load and save these fields in `BiteStore`, falling back safely when stored values are missing or invalid.
- [x] Re-run the focused domain test and then the full unit test suite.

### Task 3: Settings UI

**Files:**
- Modify: `app/src/main/kotlin/com/holdthatbite/MainActivity.kt`

- [x] Add a Material 3 exposed dropdown for plan selection.
- [x] Add a first-meal time button using `TimePickerDialog`.
- [x] Display the computed last-bite time, including the `次日` prefix when needed.
- [x] Keep touch targets at least 44dp and use existing color tokens.

### Task 4: Final Verification

**Files:**
- Verify repository only.

- [x] Run `.\gradlew.bat testDebugUnitTest`.
- [x] Run `.\gradlew.bat assembleDebug`.
- [x] If MuMu emulator is listening, install the debug APK with `adb install -r`.
