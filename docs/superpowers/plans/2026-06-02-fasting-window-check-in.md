# Fasting Window Check-In Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Show final check-in actions only when they make sense, and treat unrecorded past days as soft successes.

**Architecture:** Extend the fasting domain model with eating-window checks and soft-default status helpers. Update home UI to render calendar cells and day summaries from effective status, while keeping persisted `BiteRecord` nullable so soft defaults are not saved.

**Tech Stack:** Android, Kotlin, Jetpack Compose, Material 3, JUnit 4.

---

### Task 1: Domain Rules

**Files:**
- Modify: `app/src/main/kotlin/com/holdthatbite/domain/FastingPlan.kt`
- Test: `app/src/test/kotlin/com/holdthatbite/domain/FastingPlanTest.kt`

- [x] Add tests for eating-window detection, fasting-window detection, and cross-midnight eating windows.
- [x] Implement `isEatingWindow`.
- [x] Run focused unit tests.

### Task 2: Home UI Rules

**Files:**
- Modify: `app/src/main/kotlin/com/holdthatbite/MainActivity.kt`

- [x] Change record creation to accept a target date.
- [x] Render effective default-kept status for past unrecorded dates without saving records.
- [x] Hide final check-in buttons during today's eating window and show a hint card.
- [x] Show final check-in buttons for today in fasting window and for selected past dates.

### Task 3: Verification

**Files:**
- Verify repository only.

- [x] Run `.\gradlew.bat testDebugUnitTest assembleDebug` with project `GRADLE_USER_HOME`.
- [x] Install debug APK to MuMu when available.
