# Umeng Analytics Consent Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 接入友盟+统计、隐私同意弹窗、匿名统计开关和关键功能埋点。

**Architecture:** 使用一个 `AnalyticsTracker` 封装友盟 SDK 初始化与事件上报，`BiteStore` 保存隐私同意与统计开关状态，`MainActivity` 只负责在用户动作发生时调用事件方法。

**Tech Stack:** Android、Kotlin、Jetpack Compose、SharedPreferences、友盟 U-App SDK。

---

### Task 1: SDK And Permissions

**Files:**
- Modify: `app/build.gradle.kts`
- Modify: `app/src/main/AndroidManifest.xml`

- [x] Add `com.umeng.umsdk:common:9.9.1` and `com.umeng.umsdk:asms:1.8.7.2`; do not add `uyumao`.
- [x] Add `android.permission.INTERNET` and `android.permission.ACCESS_NETWORK_STATE`.

### Task 2: Persistent Consent State

**Files:**
- Modify: `app/src/main/kotlin/com/holdthatbite/domain/Models.kt`
- Modify: `app/src/main/kotlin/com/holdthatbite/data/BiteStore.kt`

- [x] Add `privacyPolicyAccepted` and `analyticsEnabled` to `AppSettings`.
- [x] Load and save both values through existing SharedPreferences.

### Task 3: Analytics Wrapper

**Files:**
- Create: `app/src/main/kotlin/com/holdthatbite/analytics/AnalyticsTracker.kt`

- [x] Delay `UMConfigure.init` until analytics is enabled.
- [x] Submit privacy grant before initialization.
- [x] Guard event reporting when analytics is disabled.

### Task 4: Compose Integration

**Files:**
- Modify: `app/src/main/kotlin/com/holdthatbite/MainActivity.kt`

- [x] Show first-run privacy dialog.
- [x] Add settings switch and policy viewer.
- [x] Track configured events only after analytics is enabled.

### Task 5: Verification

**Files:**
- Build output under `app/build/outputs`.

- [x] Run `.\gradlew.bat assembleDebug`.
- [x] If build succeeds, check for a MuMu emulator device and install the debug APK when available.
- [x] Dump APK permissions and confirm no precise location, coarse location, app list, or ad ID permission is requested.
