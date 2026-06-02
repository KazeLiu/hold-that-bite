# Home Widget V1 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the first `4x2` Android home-screen widget for Hold That Bite, showing today's eating/fasting window and quick actions.

**Architecture:** Add a testable `HomeWidgetModel` that converts `AppSettings` into widget text and button visibility, then add a native `AppWidgetProvider` with RemoteViews for lifecycle updates and button clicks. Reuse existing shortcut actions for app-opening flows and `BiteStore` for direct widget writes.

**Tech Stack:** Kotlin, Android AppWidgetProvider, RemoteViews XML, SharedPreferences-backed `BiteStore`, JUnit 4, Gradle Android plugin.

---

### Task 1: Widget Display Model

**Files:**
- Create: `app/src/main/kotlin/com/holdthatbite/HomeWidgetModel.kt`
- Test: `app/src/test/kotlin/com/holdthatbite/HomeWidgetModelTest.kt`

- [ ] **Step 1: Write failing tests**

```kotlin
package com.holdthatbite

import com.holdthatbite.domain.AppSettings
import com.holdthatbite.domain.FastingPlan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeWidgetModelTest {
    @Test
    fun defaultSettingsShowSixteenEightEatingAndFastingWindow() {
        val model = HomeWidgetModel.from(AppSettings())

        assertEquals("吃饭 09:00 - 17:00", model.eatingWindowText)
        assertEquals("17:00 后禁食", model.fastingHintText)
        assertFalse(model.showWeightButton)
    }

    @Test
    fun weightButtonShowsOnlyWhenWeightTrendIsEnabled() {
        val model = HomeWidgetModel.from(AppSettings(weightTrendEnabled = true))

        assertTrue(model.showWeightButton)
    }

    @Test
    fun crossDayLastBiteKeepsNextDayText() {
        val model = HomeWidgetModel.from(
            AppSettings(
                fastingPlan = FastingPlan.TWELVE_TWELVE,
                firstMealHour = 18,
                firstMealMinute = 30,
            )
        )

        assertEquals("吃饭 18:30 - 次日 06:30", model.eatingWindowText)
        assertEquals("次日 06:30 后禁食", model.fastingHintText)
    }
}
```

- [ ] **Step 2: Run tests to verify RED**

Run: `.\gradlew.bat testDebugUnitTest --tests com.holdthatbite.HomeWidgetModelTest`

Expected: fail because `HomeWidgetModel` is unresolved.

- [ ] **Step 3: Add minimal display model**

```kotlin
package com.holdthatbite

import com.holdthatbite.domain.AppSettings
import com.holdthatbite.domain.MealTime

internal data class HomeWidgetModel(
    val eatingWindowText: String,
    val fastingHintText: String,
    val showWeightButton: Boolean,
) {
    companion object {
        fun from(settings: AppSettings): HomeWidgetModel {
            val firstMeal = MealTime(settings.firstMealHour, settings.firstMealMinute)
            val lastBite = settings.fastingPlan.lastBiteTime(firstMeal)
            return HomeWidgetModel(
                eatingWindowText = "吃饭 ${firstMeal.displayText} - ${lastBite.displayText}",
                fastingHintText = "${lastBite.displayText} 后禁食",
                showWeightButton = settings.weightTrendEnabled,
            )
        }
    }
}
```

- [ ] **Step 4: Run tests to verify GREEN**

Run: `.\gradlew.bat testDebugUnitTest --tests com.holdthatbite.HomeWidgetModelTest`

Expected: pass.

### Task 2: Widget Provider and Resources

**Files:**
- Create: `app/src/main/kotlin/com/holdthatbite/HomeWidgetProvider.kt`
- Create: `app/src/main/res/layout/home_widget.xml`
- Create: `app/src/main/res/xml/home_widget_info.xml`
- Modify: `app/src/main/AndroidManifest.xml`
- Modify: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: Add AppWidgetProvider**

Implement `HomeWidgetProvider` with these constants:

```kotlin
internal const val ACTION_WIDGET_SNACK_REFUSAL = "com.holdthatbite.widget.action.SNACK_REFUSAL"
internal const val ACTION_WIDGET_KEPT_CHECK_IN = "com.holdthatbite.widget.action.KEPT_CHECK_IN"
internal const val ACTION_WIDGET_REFRESH = "com.holdthatbite.widget.action.REFRESH"
```

Provider behavior:

- `onUpdate` calls `updateAll(context)`.
- `ACTION_WIDGET_SNACK_REFUSAL` increments today's snack refusal and refreshes all widgets.
- `ACTION_WIDGET_KEPT_CHECK_IN` records `BiteStatus.KEPT` when current time is outside the eating window; otherwise opens `MainActivity` with `LauncherShortcuts.ACTION_KEPT_CHECK_IN`.
- `ACTION_WIDGET_REFRESH` refreshes all widgets.
- RemoteViews uses `HomeWidgetModel.from(store.loadSettings())` and hides `widget_weight_button` when `showWeightButton=false`.

- [ ] **Step 2: Add widget layout**

Create a compact `RemoteViews` XML layout with root id `widget_root`, text ids `widget_title`, `widget_eating_window`, `widget_fasting_hint`, and button ids `widget_snack_button`, `widget_kept_button`, `widget_weight_button`.

- [ ] **Step 3: Add widget metadata**

Create `home_widget_info.xml` with a `4x2` default target:

```xml
<appwidget-provider
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:minWidth="250dp"
    android:minHeight="110dp"
    android:targetCellWidth="4"
    android:targetCellHeight="2"
    android:updatePeriodMillis="0"
    android:initialLayout="@layout/home_widget"
    android:resizeMode="horizontal|vertical"
    android:widgetCategory="home_screen" />
```

- [ ] **Step 4: Register provider and strings**

Register a receiver for `.HomeWidgetProvider` in `AndroidManifest.xml` with `android.appwidget.action.APPWIDGET_UPDATE` metadata, and add widget label/button strings in `strings.xml`.

- [ ] **Step 5: Run compile check**

Run: `.\gradlew.bat testDebugUnitTest --tests com.holdthatbite.HomeWidgetModelTest`

Expected: pass and compile provider/resources.

### Task 3: App Integration Refresh

**Files:**
- Modify: `app/src/main/kotlin/com/holdthatbite/MainActivity.kt`

- [ ] **Step 1: Refresh widget after App writes**

Call `HomeWidgetProvider.updateAll(activity)` after:

- `saveSettings(nextSettings)`
- successful `recordCheckIn`
- successful `recordSnackRefusal`
- successful `undoSnackRefusal`

- [ ] **Step 2: Reuse existing shortcut entry points**

Keep `LauncherShortcuts.ACTION_KEPT_CHECK_IN` and `LauncherShortcuts.ACTION_RECORD_WEIGHT` for widget actions that need the app UI, so encouragement and weight-sheet behavior remain the same as existing launcher shortcuts.

- [ ] **Step 3: Run focused tests**

Run: `.\gradlew.bat testDebugUnitTest --tests com.holdthatbite.HomeWidgetModelTest --tests com.holdthatbite.LauncherShortcutsTest`

Expected: pass.

### Task 4: Build and Emulator Install

**Files:**
- No planned source edits unless verification reveals compile errors.

- [ ] **Step 1: Run full unit tests**

Run: `.\gradlew.bat testDebugUnitTest`

Expected: pass.

- [ ] **Step 2: Build debug APK**

Run: `.\gradlew.bat assembleDebug`

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Install to MuMu if available**

Check likely MuMu adb ports with:

```powershell
Get-NetTCPConnection -State Listen | Where-Object { $_.LocalPort -in 7555,16384,16416,16448 } | Select-Object LocalAddress,LocalPort
```

If a MuMu port is listening, run:

```powershell
adb connect 127.0.0.1:<port>
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Expected: `Success`. If no MuMu port is listening, report that installation was skipped because no MuMu adb port was open.

### Task 5: Commit Delivery

**Files:**
- Review all changed files with `git diff` and `git status --short`.

- [ ] **Step 1: Commit plan**

Stage only `docs/superpowers/plans/2026-06-02-home-widget.md` and commit:

```powershell
git add docs/superpowers/plans/2026-06-02-home-widget.md
git commit -m "docs: 规划桌面小组件实现"
```

- [ ] **Step 2: Commit widget feature**

After verification, stage widget implementation and tests only, then commit:

```powershell
git add app/src/main/kotlin/com/holdthatbite/HomeWidgetModel.kt app/src/main/kotlin/com/holdthatbite/HomeWidgetProvider.kt app/src/main/res/layout/home_widget.xml app/src/main/res/xml/home_widget_info.xml app/src/main/AndroidManifest.xml app/src/main/res/values/strings.xml app/src/main/kotlin/com/holdthatbite/MainActivity.kt app/src/test/kotlin/com/holdthatbite/HomeWidgetModelTest.kt
git commit -m "feat: 添加桌面小组件第一版"
```
