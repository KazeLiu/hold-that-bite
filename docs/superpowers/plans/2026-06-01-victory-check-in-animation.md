# Victory Check-In Animation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add the approved `守住了` emoji firework and one-turn victory card animation to the production Android Compose app.

**Architecture:** Keep the behavior in `MainActivity.kt` because the current app already keeps screen-level Compose UI there. Add small local models for emoji particles and title confetti, replace only the `守住了` button rendering, and add a new victory-card dialog for kept check-ins while preserving the existing bottom sheet for missed check-ins.

**Tech Stack:** Android Kotlin, Jetpack Compose Material 3, Compose animation, Gradle Android plugin.

---

### Task 1: Wire Kept Check-In Celebration

**Files:**
- Modify: `app/src/main/kotlin/com/holdthatbite/MainActivity.kt`

- [ ] **Step 1: Add animation imports**

Add Compose imports for `Animatable`, `LinearEasing`, `LaunchedEffect`, `mutableStateListOf`, `pointerInput`, `detectTapGestures`, and `Dialog`.

- [ ] **Step 2: Add local particle models**

Add immutable data classes for button emoji particles and title confetti specs near the color constants.

- [ ] **Step 3: Replace the kept button**

Replace the existing `Button("守住了")` with a `CelebrationCheckInButton` composable that emits tap and long-press particles before calling `onCheckIn(BiteStatus.KEPT)`.

### Task 2: Add Victory Card Dialog

**Files:**
- Modify: `app/src/main/kotlin/com/holdthatbite/MainActivity.kt`

- [ ] **Step 1: Route kept records to the new dialog**

When `activeSheet == CHECK_IN_SUPPLEMENT`, show `VictoryCheckInSupplementDialog` for `BiteStatus.KEPT` and keep `CheckInSupplementSheet` for other statuses.

- [ ] **Step 2: Implement one-turn card intro**

Use an `Animatable` from `0f` to `1f` over `780ms` with linear easing. Map progress to `rotationY = 360f * progress`, `scale = 0.12f + 0.88f * progress`, and opacity.

- [ ] **Step 3: Add glowing title and falling confetti**

Render large `守住了` text centered at the top of the card. Confetti should fall only within the title area and continuously loop while the dialog is visible.

### Task 3: Verify

**Files:**
- No source changes expected beyond Task 1 and Task 2.

- [ ] **Step 1: Run Gradle tests**

Run: `.\gradlew.bat testDebugUnitTest`

- [ ] **Step 2: Run debug build**

Run: `.\gradlew.bat assembleDebug`

- [ ] **Step 3: Check emulator install target**

Check common MuMu ports with `adb devices`; install the debug APK if a MuMu emulator is present.
