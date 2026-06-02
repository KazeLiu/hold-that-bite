package com.holdthatbite

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.app.TimePickerDialog
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Undo
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.holdthatbite.analytics.AnalyticsEvent
import com.holdthatbite.analytics.AnalyticsTracker
import com.holdthatbite.data.BiteStore
import com.holdthatbite.domain.AppSettings
import com.holdthatbite.domain.BiteCalendar
import com.holdthatbite.domain.BiteRecord
import com.holdthatbite.domain.BiteStatus
import com.holdthatbite.domain.CalendarDay
import com.holdthatbite.domain.CalendarMode
import com.holdthatbite.domain.FastingPlan
import com.holdthatbite.domain.MealTime
import com.holdthatbite.domain.ThemeMode
import com.holdthatbite.domain.WeightEntry
import com.holdthatbite.domain.WeightUnit
import com.holdthatbite.domain.WeightTrend
import com.holdthatbite.ui.AppColorPalette
import com.holdthatbite.ui.AppColors
import com.holdthatbite.ui.CheckInSupplement
import com.holdthatbite.ui.WeightChartModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.min

class MainActivity : ComponentActivity() {
    private var launcherAction by mutableStateOf<LauncherShortcutAction?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcherAction = LauncherShortcuts.actionFrom(intent?.action)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.WHITE
        setContent {
            HoldThatBiteApp(
                activity = this,
                launcherAction = launcherAction,
                onLauncherActionConsumed = { launcherAction = null },
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        launcherAction = LauncherShortcuts.actionFrom(intent.action)
    }
}

private enum class AppTab(val label: String) {
    TREND("趋势"),
    HOME("主页"),
    SETTINGS("设置")
}

private enum class ActiveSheet {
    CHECK_IN_SUPPLEMENT,
    WEIGHT_ONLY,
    NOTE_EDIT
}

private val LocalAppPalette = staticCompositionLocalOf { AppColors.LightPalette }
private val Primary: Color @Composable get() = LocalAppPalette.current.primary
private val Background: Color @Composable get() = LocalAppPalette.current.background
private val SurfaceColor: Color @Composable get() = LocalAppPalette.current.surface
private val SurfaceSubtle: Color @Composable get() = LocalAppPalette.current.surfaceSubtle
private val TextPrimary: Color @Composable get() = LocalAppPalette.current.textPrimary
private val TextSecondary: Color @Composable get() = LocalAppPalette.current.textSecondary
private val Border: Color @Composable get() = LocalAppPalette.current.border
private val Success: Color @Composable get() = LocalAppPalette.current.success
private val SuccessSoft: Color @Composable get() = LocalAppPalette.current.successSoft
private val Missed: Color @Composable get() = LocalAppPalette.current.missed
private val MissedSoft: Color @Composable get() = LocalAppPalette.current.missedSoft
private val Neutral: Color @Composable get() = LocalAppPalette.current.neutral
private const val TabTransitionMillis = 240
private val HomeFixedActionsFallbackPadding = 118.dp
private val HomeFixedActionsGap = 10.dp
private val HomeMonthCalendarHeight = 336.dp
private val HomeWeekCalendarHeight = 188.dp
private val CalendarCellGap = 8.dp
private const val ProjectGitHubUrl = "https://github.com/KazeLiu/hold-that-bite"
private const val UmengPrivacyPolicyUrl = "https://www.umeng.com/page/policy"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HoldThatBiteApp(
    activity: MainActivity,
    launcherAction: LauncherShortcutAction?,
    onLauncherActionConsumed: () -> Unit,
) {
    val store = remember { BiteStore(activity) }
    val analytics = remember { AnalyticsTracker(activity) }
    var settings by remember { mutableStateOf(store.loadSettings()) }
    var records by remember { mutableStateOf(store.loadRecords()) }
    var snackRefusals by remember { mutableStateOf(store.loadSnackRefusals()) }
    var weights by remember { mutableStateOf(store.loadWeights()) }
    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var visibleMonth by remember { mutableStateOf(YearMonth.now()) }
    var activeSheet by remember { mutableStateOf<ActiveSheet?>(null) }
    var pendingRecord by remember { mutableStateOf<BiteRecord?>(null) }
    var confirmMissedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showPrivacyDialog by remember { mutableStateOf(!settings.privacyPolicyAccepted) }
    var showPolicyDetails by remember { mutableStateOf(false) }
    var showShortcutEncouragement by remember { mutableStateOf(false) }
    var appOpenTracked by remember { mutableStateOf(false) }

    fun saveSettings(nextSettings: AppSettings) {
        settings = nextSettings
        store.saveSettings(nextSettings)
    }

    LaunchedEffect(settings.privacyPolicyAccepted, settings.analyticsEnabled) {
        val analyticsAllowed = settings.privacyPolicyAccepted && settings.analyticsEnabled
        analytics.setEnabled(analyticsAllowed)
        if (analyticsAllowed && !appOpenTracked) {
            analytics.track(AnalyticsEvent.APP_OPEN)
            appOpenTracked = true
        }
    }

    LaunchedEffect(settings.weightTrendEnabled) {
        LauncherShortcuts.publishDynamic(activity, settings)
    }

    val visibleTabs = if (settings.weightTrendEnabled) {
        listOf(AppTab.TREND, AppTab.HOME, AppTab.SETTINGS)
    } else {
        listOf(AppTab.HOME, AppTab.SETTINGS)
    }
    if (currentTab == AppTab.TREND && !settings.weightTrendEnabled) {
        currentTab = AppTab.HOME
    }

    fun recordCheckIn(date: LocalDate, status: BiteStatus) {
        val key = BiteCalendar.dateKey(date)
        val record = BiteRecord(
            dateKey = key,
            status = status,
            note = records[key]?.note.orEmpty(),
        )
        store.upsertRecord(record)
        records = store.loadRecords()
        pendingRecord = record
        selectedDate = date
        visibleMonth = YearMonth.from(date)
        activeSheet = ActiveSheet.CHECK_IN_SUPPLEMENT
        analytics.track(
            if (status == BiteStatus.KEPT) AnalyticsEvent.BITE_KEPT else AnalyticsEvent.BITE_MISSED
        )
    }

    fun recordSnackRefusal() {
        val today = LocalDate.now()
        val key = BiteCalendar.dateKey(today)
        store.incrementSnackRefusal(key)
        snackRefusals = store.loadSnackRefusals()
        selectedDate = today
        visibleMonth = YearMonth.from(today)
        analytics.track(AnalyticsEvent.SNACK_REFUSAL_ADDED)
    }

    fun undoSnackRefusal() {
        val today = LocalDate.now()
        val key = BiteCalendar.dateKey(today)
        store.undoSnackRefusal(key)
        snackRefusals = store.loadSnackRefusals()
        selectedDate = today
        visibleMonth = YearMonth.from(today)
    }

    fun canRecordKeptNow(): Boolean {
        val now = LocalTime.now()
        val firstMeal = MealTime(settings.firstMealHour, settings.firstMealMinute)
        val nowMealTime = MealTime(now.hour, now.minute)
        return !settings.fastingPlan.isEatingWindow(firstMeal, nowMealTime)
    }

    fun handleLauncherShortcut(action: LauncherShortcutAction) {
        when (action) {
            LauncherShortcutAction.SNACK_REFUSAL -> {
                currentTab = AppTab.HOME
                recordSnackRefusal()
            }
            LauncherShortcutAction.KEPT_CHECK_IN -> {
                val today = LocalDate.now()
                currentTab = AppTab.HOME
                selectedDate = today
                visibleMonth = YearMonth.from(today)
                if (canRecordKeptNow()) {
                    recordCheckIn(today, BiteStatus.KEPT)
                } else {
                    showShortcutEncouragement = true
                }
            }
            LauncherShortcutAction.RECORD_WEIGHT -> {
                if (settings.weightTrendEnabled) {
                    if (currentTab != AppTab.TREND) {
                        analytics.track(AnalyticsEvent.WEIGHT_TREND_OPENED)
                    }
                    currentTab = AppTab.TREND
                    activeSheet = ActiveSheet.WEIGHT_ONLY
                }
            }
        }
    }

    LaunchedEffect(launcherAction) {
        launcherAction?.let { action ->
            handleLauncherShortcut(action)
            onLauncherActionConsumed()
        }
    }

    LaunchedEffect(showShortcutEncouragement) {
        if (showShortcutEncouragement) {
            delay(3200)
            showShortcutEncouragement = false
        }
    }

    val useDarkTheme = settings.themeMode.shouldUseDarkTheme(isSystemInDarkTheme())
    val palette = if (useDarkTheme) AppColors.DarkPalette else AppColors.LightPalette
    val colorScheme = if (useDarkTheme) {
        darkColorScheme(
            primary = palette.primary,
            background = palette.background,
            surface = palette.surface,
            surfaceVariant = palette.surfaceSubtle,
            onSurface = palette.textPrimary,
            onSurfaceVariant = palette.textSecondary,
            outline = palette.border,
        )
    } else {
        lightColorScheme(
            primary = palette.primary,
            background = palette.background,
            surface = palette.surface,
            surfaceVariant = palette.surfaceSubtle,
            onSurface = palette.textPrimary,
            onSurfaceVariant = palette.textSecondary,
            outline = palette.border,
        )
    }

    SideEffect {
        activity.window.statusBarColor = android.graphics.Color.TRANSPARENT
        activity.window.navigationBarColor = palette.surface.toArgb()
        WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
            isAppearanceLightStatusBars = !useDarkTheme
            isAppearanceLightNavigationBars = !useDarkTheme
        }
    }

    CompositionLocalProvider(LocalAppPalette provides palette) {
        MaterialTheme(colorScheme = colorScheme) {
            Surface(color = Background, modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background)
                        .statusBarsPadding()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clipToBounds()
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = {
                                val forward = targetState.ordinal > initialState.ordinal
                                val enterOffset: (Int) -> Int = { width -> if (forward) width else -width }
                                val exitOffset: (Int) -> Int = { width -> if (forward) -width else width }
                                val motion = tween<androidx.compose.ui.unit.IntOffset>(
                                    durationMillis = TabTransitionMillis,
                                    easing = FastOutSlowInEasing
                                )
                                val fadeMotion = tween<Float>(
                                    durationMillis = TabTransitionMillis,
                                    easing = FastOutSlowInEasing
                                )

                                (slideInHorizontally(animationSpec = motion, initialOffsetX = enterOffset) +
                                    fadeIn(animationSpec = fadeMotion)) togetherWith
                                    (slideOutHorizontally(animationSpec = motion, targetOffsetX = exitOffset) +
                                        fadeOut(animationSpec = fadeMotion))
                            },
                            modifier = Modifier.fillMaxSize(),
                            label = "tab-page-transition",
                        ) { tab ->
                            when (tab) {
                        AppTab.TREND -> TrendPage(
                            weights = weights,
                            targetWeightKg = settings.targetWeightKg,
                            weightUnit = settings.weightUnit,
                            onRecordWeight = { activeSheet = ActiveSheet.WEIGHT_ONLY },
                            onTargetWeightChanged = { target ->
                                saveSettings(settings.copy(targetWeightKg = target))
                            },
                            onDeleteWeight = { entry ->
                                store.deleteWeight(entry.timestampMillis)
                                weights = store.loadWeights()
                            },
                        )
                                AppTab.HOME -> HomePage(
                                    settings = settings,
                                    records = records,
                                    snackRefusals = snackRefusals,
                                    weights = weights,
                                    selectedDate = selectedDate,
                                    visibleMonth = visibleMonth,
                                    onPrevious = {
                                        if (settings.calendarMode == CalendarMode.MONTH) {
                                            visibleMonth = visibleMonth.minusMonths(1)
                                            selectedDate = clampDateToMonth(selectedDate, visibleMonth)
                                        } else {
                                            selectedDate = selectedDate.minusWeeks(1)
                                            visibleMonth = YearMonth.from(selectedDate)
                                        }
                                    },
                                    onNext = {
                                        if (settings.calendarMode == CalendarMode.MONTH) {
                                            visibleMonth = visibleMonth.plusMonths(1)
                                            selectedDate = clampDateToMonth(selectedDate, visibleMonth)
                                        } else {
                                            selectedDate = selectedDate.plusWeeks(1)
                                            visibleMonth = YearMonth.from(selectedDate)
                                        }
                                    },
                                    onSelectDate = {
                                        selectedDate = it
                                        visibleMonth = YearMonth.from(it)
                                    },
                                    onCheckIn = { date, status ->
                                        val key = BiteCalendar.dateKey(date)
                                        if (status == BiteStatus.MISSED && records[key]?.status == BiteStatus.KEPT) {
                                            selectedDate = date
                                            visibleMonth = YearMonth.from(date)
                                            confirmMissedDate = date
                                        } else {
                                            recordCheckIn(date, status)
                                        }
                                    },
                                    onEditNote = { record ->
                                        pendingRecord = record
                                        activeSheet = ActiveSheet.NOTE_EDIT
                                    },
                                    onSnackRefusal = ::recordSnackRefusal,
                                    onUndoSnackRefusal = ::undoSnackRefusal,
                                )
                                AppTab.SETTINGS -> SettingsPage(
                                    settings = settings,
                                    onSettingsChanged = {
                                        val wasWeightTrendEnabled = settings.weightTrendEnabled
                                        saveSettings(it)
                                        if (!wasWeightTrendEnabled && it.weightTrendEnabled) {
                                            analytics.track(AnalyticsEvent.WEIGHT_TREND_ENABLED)
                                        }
                                        if (wasWeightTrendEnabled && !it.weightTrendEnabled) {
                                            analytics.track(AnalyticsEvent.WEIGHT_TREND_DISABLED)
                                        }
                                    },
                                    onPolicyRequested = {
                                        showPolicyDetails = true
                                        analytics.track(AnalyticsEvent.PRIVACY_POLICY_OPENED)
                                    },
                                )
                            }
                        }
                    }

                    AppBottomNav(
                        visibleTabs = visibleTabs,
                        currentTab = currentTab,
                        onTabSelected = { tab ->
                            if (tab == AppTab.SETTINGS && currentTab != AppTab.SETTINGS) {
                                analytics.track(AnalyticsEvent.SETTINGS_OPENED)
                            }
                            if (tab == AppTab.TREND && currentTab != AppTab.TREND) {
                                analytics.track(AnalyticsEvent.WEIGHT_TREND_OPENED)
                            }
                            currentTab = tab
                        },
                        modifier = Modifier.navigationBarsPadding(),
                    )
                }
            }

            when (activeSheet) {
                ActiveSheet.CHECK_IN_SUPPLEMENT -> {
                    val supplementDone: (CheckInSupplement) -> Unit = { supplement ->
                        supplement.note?.let { note ->
                            pendingRecord?.let { record -> store.upsertRecord(record.copy(note = note)) }
                            records = store.loadRecords()
                        }
                        supplement.weightKg?.let { value ->
                            if (value > 0.0 && value < 500.0) {
                                store.addWeight(WeightEntry(System.currentTimeMillis(), value))
                                weights = store.loadWeights()
                                analytics.track(AnalyticsEvent.WEIGHT_RECORD_CREATED)
                            }
                        }
                        activeSheet = null
                    }
                    if (pendingRecord?.status == BiteStatus.KEPT) {
                        VictoryCheckInSupplementDialog(
                            initialNote = pendingRecord?.note.orEmpty(),
                            includeWeight = settings.askWeightAfterCheckIn,
                            weightUnit = settings.weightUnit,
                            onDismiss = { activeSheet = null },
                            onDone = supplementDone,
                        )
                    } else {
                        CheckInSupplementSheet(
                            initialNote = pendingRecord?.note.orEmpty(),
                            includeWeight = settings.askWeightAfterCheckIn,
                            weightUnit = settings.weightUnit,
                            onDismiss = { activeSheet = null },
                            onDone = supplementDone,
                        )
                    }
                }
                ActiveSheet.WEIGHT_ONLY -> WeightSheet(
                    weightUnit = settings.weightUnit,
                    onDismiss = { activeSheet = null },
                    onSave = { entry ->
                        if (entry != null && entry.weightKg > 0.0 && entry.weightKg < 500.0) {
                            store.addWeight(entry)
                            weights = store.loadWeights()
                            analytics.track(AnalyticsEvent.WEIGHT_RECORD_CREATED)
                        }
                        activeSheet = null
                    },
                )
                ActiveSheet.NOTE_EDIT -> NoteEditSheet(
                    initialNote = pendingRecord?.note.orEmpty(),
                    onDismiss = {
                        pendingRecord = null
                        activeSheet = null
                    },
                    onSave = { note ->
                        pendingRecord?.let { record ->
                            store.upsertRecord(record.copy(note = note.trim()))
                            records = store.loadRecords()
                        }
                        pendingRecord = null
                        activeSheet = null
                    },
                )
                null -> Unit
            }

            confirmMissedDate?.let { date ->
                AlertDialog(
                    onDismissRequest = { confirmMissedDate = null },
                    title = { Text("是不是不小心吃了？") },
                    text = { Text("辛苦了，能坚持到这里已经很不容易了。要把这天改成没守住吗？") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                confirmMissedDate = null
                                recordCheckIn(date, BiteStatus.MISSED)
                            }
                        ) {
                            Text("是，改成没守住", color = Missed)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { confirmMissedDate = null }) {
                            Text("先保留守住了", color = TextSecondary)
                        }
                    },
                )
            }

            if (showShortcutEncouragement) {
                AlertDialog(
                    onDismissRequest = { showShortcutEncouragement = false },
                    title = { Text("快成功了") },
                    text = { Text("我知道你快成功了，再坚持坚持") },
                    confirmButton = {
                        TextButton(onClick = { showShortcutEncouragement = false }) {
                            Text("确定", color = Primary)
                        }
                    },
                )
            }

            if (showPrivacyDialog) {
                PrivacyConsentDialog(
                    onEnableAnalytics = {
                        showPrivacyDialog = false
                        saveSettings(settings.copy(privacyPolicyAccepted = true, analyticsEnabled = true))
                    },
                    onLocalOnly = {
                        showPrivacyDialog = false
                        saveSettings(settings.copy(privacyPolicyAccepted = true, analyticsEnabled = false))
                    },
                    onShowPolicy = {
                        showPolicyDetails = true
                    },
                )
            }

            if (showPolicyDetails) {
                PrivacyPolicyDialog(onDismiss = { showPolicyDetails = false })
            }
        }
    }
    }
}

@Composable
private fun HomePage(
    settings: AppSettings,
    records: Map<String, BiteRecord>,
    snackRefusals: Map<String, Int>,
    weights: List<WeightEntry>,
    selectedDate: LocalDate,
    visibleMonth: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onCheckIn: (LocalDate, BiteStatus) -> Unit,
    onEditNote: (BiteRecord) -> Unit,
    onSnackRefusal: () -> Unit,
    onUndoSnackRefusal: () -> Unit,
) {
    val today = LocalDate.now()
    val now = LocalTime.now()
    val firstMeal = MealTime(settings.firstMealHour, settings.firstMealMinute)
    val nowMealTime = MealTime(now.hour, now.minute)
    val inEatingWindow = settings.fastingPlan.isEatingWindow(firstMeal, nowMealTime)
    val lastBiteTime = settings.fastingPlan.lastBiteTime(firstMeal)
    val selectedRecord = records[BiteCalendar.dateKey(selectedDate)]
    val showFinalCheckInActions = selectedDate.isBefore(today) || (selectedDate == today && !inEatingWindow)
    val showEatingWindowHint = selectedDate == today && inEatingWindow
    val selectedSnackRefusalCount = snackRefusals[BiteCalendar.dateKey(selectedDate)] ?: 0
    val todaySnackRefusalCount = snackRefusals[BiteCalendar.dateKey(today)] ?: 0
    val density = LocalDensity.current
    var fixedActionsHeightPx by remember { mutableStateOf(0) }
    val fixedActionsPadding = if (fixedActionsHeightPx > 0) {
        with(density) { fixedActionsHeightPx.toDp() } + HomeFixedActionsGap
    } else {
        HomeFixedActionsFallbackPadding
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = fixedActionsPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HeaderBlock(
                title = "守住这口",
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CalendarView(
                    settings = settings,
                    records = records,
                    today = today,
                    selectedDate = selectedDate,
                    visibleMonth = visibleMonth,
                    onPrevious = onPrevious,
                    onNext = onNext,
                    onSelectDate = onSelectDate,
                    modifier = Modifier.height(
                        if (settings.calendarMode == CalendarMode.MONTH) {
                            HomeMonthCalendarHeight
                        } else {
                            HomeWeekCalendarHeight
                        }
                    ),
                )

                DaySummary(
                    selectedDate = selectedDate,
                    record = selectedRecord,
                    effectiveStatus = effectiveBiteStatus(selectedDate, selectedRecord, today),
                    softDefaultKept = isSoftDefaultKept(selectedDate, selectedRecord, today),
                    snackRefusalCount = selectedSnackRefusalCount,
                    weightSummary = if (settings.weightTrendEnabled) buildWeightDaySummary(selectedDate, weights) else null,
                    weightUnit = settings.weightUnit,
                    onEditNote = onEditNote,
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .onSizeChanged { fixedActionsHeightPx = it.height },
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SnackRefusalAction(
                count = todaySnackRefusalCount,
                onAdd = onSnackRefusal,
                onUndo = onUndoSnackRefusal,
            )

            when {
                showFinalCheckInActions -> CelebrationCheckInActions(
                    onMissed = { onCheckIn(selectedDate, BiteStatus.MISSED) },
                    onKept = { onCheckIn(selectedDate, BiteStatus.KEPT) },
                )
                showEatingWindowHint -> EatingWindowHint(
                    lastBiteText = lastBiteTime.displayText,
                    planLabel = settings.fastingPlan.label,
                )
            }
        }
    }
}

@Composable
private fun EatingWindowHint(lastBiteText: String, planLabel: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Neutral)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("现在是正常吃饭时间", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text("$lastBiteText 后回来记录今天有没有守住", color = TextSecondary, fontSize = 13.sp)
            }
            Text(planLabel, color = Primary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun CalendarView(
    settings: AppSettings,
    records: Map<String, BiteRecord>,
    today: LocalDate,
    selectedDate: LocalDate,
    visibleMonth: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val days = if (settings.calendarMode == CalendarMode.MONTH) {
        BiteCalendar.monthGrid(visibleMonth)
    } else {
        BiteCalendar.weekGrid(selectedDate)
    }
    val rows = days.chunked(7)
    val period = CalendarPeriodView(
        title = if (settings.calendarMode == CalendarMode.MONTH) {
            "${visibleMonth.year}年${visibleMonth.monthValue}月"
        } else {
            formatWeekTitle(selectedDate)
        },
        sortDate = if (settings.calendarMode == CalendarMode.MONTH) {
            visibleMonth.atDay(1)
        } else {
            days.first().date
        },
        rows = rows,
    )
    val density = LocalDensity.current
    val swipeThresholdPx = remember(density) { with(density) { 48.dp.toPx() } }
    var dragOffsetX by remember { mutableStateOf(0f) }
    val periodTransitionSpec: AnimatedContentTransitionScope<CalendarPeriodView>.() -> ContentTransform = {
        val forward = targetState.sortDate > initialState.sortDate
        val enterOffset: (Int) -> Int = { width -> if (forward) width else -width }
        val exitOffset: (Int) -> Int = { width -> if (forward) -width else width }
        val motion = tween<androidx.compose.ui.unit.IntOffset>(
            durationMillis = TabTransitionMillis,
            easing = FastOutSlowInEasing
        )
        val fadeMotion = tween<Float>(
            durationMillis = TabTransitionMillis,
            easing = FastOutSlowInEasing
        )

        (slideInHorizontally(animationSpec = motion, initialOffsetX = enterOffset) +
            fadeIn(animationSpec = fadeMotion)) togetherWith
            (slideOutHorizontally(animationSpec = motion, targetOffsetX = exitOffset) +
                fadeOut(animationSpec = fadeMotion))
    }
    val swipeModifier = Modifier.pointerInput(settings.calendarMode, selectedDate, visibleMonth) {
        detectHorizontalDragGestures(
            onDragStart = { dragOffsetX = 0f },
            onHorizontalDrag = { _, dragAmount ->
                dragOffsetX += dragAmount
            },
            onDragCancel = { dragOffsetX = 0f },
            onDragEnd = {
                when {
                    dragOffsetX <= -swipeThresholdPx -> onNext()
                    dragOffsetX >= swipeThresholdPx -> onPrevious()
                }
                dragOffsetX = 0f
            },
        )
    }

    AppCard(modifier = modifier.fillMaxWidth().then(swipeModifier)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(CalendarCellGap)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPrevious, modifier = Modifier.width(48.dp)) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "上一个周期",
                        tint = TextPrimary,
                    )
                }
                AnimatedContent(
                    targetState = period,
                    transitionSpec = periodTransitionSpec,
                    modifier = Modifier
                        .weight(1f)
                        .clipToBounds(),
                    label = "calendar-title-transition",
                ) { target ->
                    Text(
                        text = target.title,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                IconButton(onClick = onNext, modifier = Modifier.width(48.dp)) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "下一个周期",
                        tint = TextPrimary,
                    )
                }
            }

            WeekdayRow()

            AnimatedContent(
                targetState = period,
                transitionSpec = periodTransitionSpec,
                modifier = Modifier
                    .weight(1f)
                    .clipToBounds(),
                label = "calendar-grid-transition",
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(CalendarCellGap)
                ) {
                    it.rows.forEach { row ->
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(CalendarCellGap)
                        ) {
                            row.forEach { day ->
                                CalendarCell(
                                    day = day,
                                    selected = day.date == selectedDate,
                                    record = records[BiteCalendar.dateKey(day.date)],
                                    today = today,
                                    onClick = { onSelectDate(day.date) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class CalendarPeriodView(
    val title: String,
    val sortDate: LocalDate,
    val rows: List<List<CalendarDay>>,
)

@Composable
private fun WeekdayRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(CalendarCellGap)) {
        listOf("一", "二", "三", "四", "五", "六", "日").forEach {
            Text(
                text = it,
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CalendarCell(
    day: CalendarDay,
    selected: Boolean,
    record: BiteRecord?,
    today: LocalDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val effectiveStatus = effectiveBiteStatus(day.date, record, today)
    val softDefaultKept = isSoftDefaultKept(day.date, record, today)
    val fill = when (effectiveStatus) {
        BiteStatus.KEPT -> SuccessSoft
        BiteStatus.MISSED -> MissedSoft
        null -> Neutral
    }
    val statusColor = when (effectiveStatus) {
        BiteStatus.KEPT -> Success
        BiteStatus.MISSED -> Missed
        null -> Color.Transparent
    }
    val textColor = when {
        !day.inCurrentPeriod -> TextSecondary.copy(alpha = 0.45f)
        effectiveStatus == BiteStatus.KEPT -> Success
        effectiveStatus == BiteStatus.MISSED -> Missed
        else -> TextPrimary
    }
    val description = when {
        softDefaultKept -> "${day.date.monthValue}月${day.date.dayOfMonth}日，默认守住了"
        else -> when (effectiveStatus) {
        BiteStatus.KEPT -> "${day.date.monthValue}月${day.date.dayOfMonth}日，守住了"
        BiteStatus.MISSED -> "${day.date.monthValue}月${day.date.dayOfMonth}日，没守住"
        null -> "${day.date.monthValue}月${day.date.dayOfMonth}日，未记录"
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(13.dp))
            .background(if (selected) Primary.copy(alpha = 0.16f) else fill)
            .clickable(onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = textColor,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .size(if (record == null) 4.dp else 7.dp)
                    .clip(CircleShape)
                    .background(if (record == null && !softDefaultKept) Border else statusColor.copy(alpha = if (softDefaultKept) 0.65f else 1f))
            )
        }
    }
}

private fun effectiveBiteStatus(date: LocalDate, record: BiteRecord?, today: LocalDate): BiteStatus? {
    return record?.status ?: if (date.isBefore(today)) BiteStatus.KEPT else null
}

private fun isSoftDefaultKept(date: LocalDate, record: BiteRecord?, today: LocalDate): Boolean {
    return record == null && date.isBefore(today)
}

@Composable
private fun DaySummary(
    selectedDate: LocalDate,
    record: BiteRecord?,
    effectiveStatus: BiteStatus?,
    softDefaultKept: Boolean,
    snackRefusalCount: Int,
    weightSummary: WeightDaySummary?,
    weightUnit: WeightUnit,
    onEditNote: (BiteRecord) -> Unit,
) {
    val today = LocalDate.now()
    val statusText = when {
        softDefaultKept -> "默认守住了"
        else -> when (effectiveStatus) {
        BiteStatus.KEPT -> "这天守住了"
        BiteStatus.MISSED -> "这天没守住"
        null -> "这天还没记录"
        }
    }
    val defaultNote = when {
        selectedDate.isAfter(today) -> "这天还没来，放轻松"
        softDefaultKept -> "没有打卡也先算守住；如果那天没守住，可以回头改。"
        effectiveStatus == BiteStatus.KEPT -> "今天真棒！这么难的事情也做到了！"
        effectiveStatus == BiteStatus.MISSED -> "今天辛苦了，多吃点也没关系"
        else -> "没有记录，非常轻松"
    }
    val note = record?.note?.takeIf { it.isNotBlank() } ?: defaultNote
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = record?.let { { onEditNote(it) } },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${selectedDate.monthValue}月${selectedDate.dayOfMonth}日 · $statusText",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                weightSummary?.let {
                    WeightSummaryLabel(summary = it, weightUnit = weightUnit)
                }
            }
            if (snackRefusalCount > 0) {
                Text(
                    "拒绝零食 $snackRefusalCount 次",
                    color = Success,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                note,
                color = TextSecondary,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun WeightSummaryLabel(summary: WeightDaySummary, weightUnit: WeightUnit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = formatWeight(summary.weightKg, weightUnit),
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        summary.change?.let { change ->
            Text(
                text = "(${change.arrow} ${formatWeight(abs(change.deltaKg), weightUnit)})",
                color = change.color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

private data class WeightDaySummary(
    val weightKg: Double,
    val change: WeightDayChange?,
)

private data class WeightDayChange(
    val deltaKg: Double,
    val arrow: String,
    val color: Color,
)

private fun buildWeightDaySummary(selectedDate: LocalDate, weights: List<WeightEntry>): WeightDaySummary? {
    val todayMin = minWeightOnDate(weights, selectedDate) ?: return null
    val previousMin = nearestPreviousMinWeight(weights, selectedDate)
    val change = previousMin?.let { previous ->
        val delta = todayMin - previous
        when {
            delta > 0.0 -> WeightDayChange(deltaKg = delta, arrow = "↑", color = AppColors.StatusMissed)
            delta < 0.0 -> WeightDayChange(deltaKg = delta, arrow = "↓", color = AppColors.StatusKept)
            else -> null
        }
    }

    return WeightDaySummary(weightKg = todayMin, change = change)
}

private fun nearestPreviousMinWeight(weights: List<WeightEntry>, selectedDate: LocalDate): Double? {
    return weights
        .map { weightDate(it) to it.weightKg }
        .filter { (date, _) -> date.isBefore(selectedDate) }
        .groupBy(keySelector = { it.first }, valueTransform = { it.second })
        .maxByOrNull { it.key }
        ?.value
        ?.minOrNull()
}

private fun minWeightOnDate(weights: List<WeightEntry>, date: LocalDate): Double? {
    return weights
        .filter { weightDate(it) == date }
        .minOfOrNull { it.weightKg }
}

private fun weightDate(entry: WeightEntry): LocalDate {
    return Instant.ofEpochMilli(entry.timestampMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

private fun formatWeight(valueKg: Double, weightUnit: WeightUnit): String {
    val value = weightUnit.toDisplay(valueKg)
    val roundedWhole = value.toLong()
    val text = if (value == roundedWhole.toDouble()) {
        roundedWhole.toString()
    } else {
        "%.1f".format(Locale.CHINA, value)
    }
    return "$text${weightUnit.label}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteEditSheet(
    initialNote: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var note by remember(initialNote) { mutableStateOf(initialNote) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("修改备注", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("这里只改备注，体重去趋势页统一处理。", color = TextSecondary, fontSize = 14.sp)
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp),
                shape = RoundedCornerShape(18.dp),
                placeholder = { Text("备注，可留空") },
                maxLines = 4,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (initialNote.isNotBlank()) {
                    OutlinedButton(
                        onClick = { onSave("") },
                        modifier = Modifier
                            .height(44.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Missed)
                    ) {
                        Text("删除备注")
                    }
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .height(44.dp)
                        .weight(1f)
                ) {
                    Text("取消", color = TextSecondary)
                }
                Button(
                    onClick = { onSave(note) },
                    modifier = Modifier
                        .height(44.dp)
                        .weight(1.3f),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("保存", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun TrendPage(
    weights: List<WeightEntry>,
    targetWeightKg: Double?,
    weightUnit: WeightUnit,
    onRecordWeight: () -> Unit,
    onTargetWeightChanged: (Double?) -> Unit,
    onDeleteWeight: (WeightEntry) -> Unit,
) {
    val density = LocalDensity.current
    val maxTopHeightPx = with(density) { 352.dp.toPx() }
    var topCollapsePx by remember { mutableStateOf(0f) }
    var monthPickerVisible by remember { mutableStateOf(false) }
    var targetSheetVisible by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val groups = remember(weights) { buildWeightMonthGroups(weights) }
    val extremes = remember(weights) { WeightExtremes.from(weights) }
    val historyItems = remember(groups, extremes) { buildWeightHistoryItems(groups, extremes) }
    val monthIndexByMonth = remember(historyItems) {
        historyItems.mapIndexedNotNull { index, item ->
            (item as? WeightHistoryItem.MonthHeader)?.month?.let { it to index }
        }.toMap()
    }
    val topHeight by remember {
        derivedStateOf {
            with(density) { (maxTopHeightPx - topCollapsePx).coerceAtLeast(0f).toDp() }
        }
    }
    val nestedScrollConnection = remember(maxTopHeightPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y >= 0f || topCollapsePx >= maxTopHeightPx) return Offset.Zero
                val previous = topCollapsePx
                topCollapsePx = (topCollapsePx - available.y).coerceIn(0f, maxTopHeightPx)
                return Offset(x = 0f, y = -(topCollapsePx - previous))
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (available.y <= 0f || topCollapsePx <= 0f) return Offset.Zero
                val previous = topCollapsePx
                topCollapsePx = (topCollapsePx - available.y).coerceIn(0f, maxTopHeightPx)
                return Offset(x = 0f, y = previous - topCollapsePx)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .nestedScroll(nestedScrollConnection)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HeaderBlock("体重趋势")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topHeight)
                    .clipToBounds(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val chartHeight = (topHeight - 66.dp).coerceAtLeast(0.dp)
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(chartHeight)
                        .graphicsLayer { alpha = (chartHeight.value / 120f).coerceIn(0f, 1f) }
                ) {
                    WeightChart(
                        weights = weights,
                        targetWeightKg = targetWeightKg,
                        weightUnit = weightUnit,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                val buttonProgress = ((topHeight - 12.dp).value / 72f).coerceIn(0f, 1f)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { targetSheetVisible = true },
                        modifier = Modifier
                            .height(54.dp * buttonProgress)
                            .weight(1f)
                            .graphicsLayer { alpha = buttonProgress },
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        if (buttonProgress > 0.1f) {
                            Text(
                                text = targetWeightKg?.let { "目标 ${formatWeight(it, weightUnit)}" } ?: "设置目标",
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.graphicsLayer { alpha = buttonProgress }
                            )
                        }
                    }
                    Button(
                        onClick = onRecordWeight,
                        modifier = Modifier
                            .height(54.dp * buttonProgress)
                            .weight(1.3f)
                            .graphicsLayer { alpha = buttonProgress },
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        if (buttonProgress > 0.1f) {
                            Text(
                                "记录体重",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.graphicsLayer { alpha = buttonProgress }
                            )
                        }
                    }
                }
            }
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                weightHistoryItems(
                    items = historyItems,
                    weightUnit = weightUnit,
                    onDeleteWeight = onDeleteWeight,
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            FloatingActionButton(
                onClick = { monthPickerVisible = true },
                modifier = Modifier.size(width = 42.dp, height = 38.dp),
                shape = RoundedCornerShape(14.dp),
                containerColor = Primary,
                contentColor = SurfaceColor,
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "定位月份",
                    modifier = Modifier.size(20.dp),
                )
            }
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        topCollapsePx = 0f
                        listState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier.size(width = 42.dp, height = 38.dp),
                shape = RoundedCornerShape(14.dp),
                containerColor = Primary,
                contentColor = SurfaceColor,
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "回到顶部",
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        if (monthPickerVisible) {
            MonthPickerSheet(
                groups = groups,
                onDismiss = { monthPickerVisible = false },
                onSelectMonth = { month ->
                    monthPickerVisible = false
                    monthIndexByMonth[month]?.let { itemIndex ->
                        scope.launch {
                            topCollapsePx = maxTopHeightPx
                            listState.animateScrollToItem(itemIndex)
                        }
                    }
                },
            )
        }
        if (targetSheetVisible) {
            TargetWeightSheet(
                currentTarget = targetWeightKg,
                weightUnit = weightUnit,
                onDismiss = { targetSheetVisible = false },
                onSave = { target ->
                    onTargetWeightChanged(target)
                    targetSheetVisible = false
                },
            )
        }
    }
}

@Composable
private fun WeightChart(
    weights: List<WeightEntry>,
    targetWeightKg: Double?,
    weightUnit: WeightUnit,
    modifier: Modifier = Modifier,
) {
    val model = remember(weights) { WeightChartModel.from(weights) }
    var selectedIndex by remember(weights) { mutableStateOf<Int?>(null) }
    val dateFormat = remember { SimpleDateFormat("M/d HH:mm", Locale.CHINA) }
    val chartPalette = LocalAppPalette.current
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when (model) {
            WeightChartModel.Empty -> {
                Text("还没有记录，想记的时候再记。", color = TextSecondary, fontSize = 14.sp)
            }
            is WeightChartModel.Ready -> {
                val entries = model.entries
                val selectedEntry = selectedIndex?.let { entries.getOrNull(it) } ?: entries.last()

                Text(
                    text = formatWeight(selectedEntry.weightKg, weightUnit),
                    color = Primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 4.dp)
                )
                Text(
                    text = dateFormat.format(Date(selectedEntry.timestampMillis)),
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 4.dp, top = 4.dp)
                )
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 30.dp),
                    factory = { context ->
                        LineChart(context).apply {
                            configureWeightLineChart(
                                entries = entries,
                                dateFormat = dateFormat,
                                targetWeightKg = targetWeightKg,
                                weightUnit = weightUnit,
                                palette = chartPalette,
                                onSelected = { selectedIndex = it },
                            )
                        }
                    },
                    update = { chart ->
                        chart.configureWeightLineChart(
                            entries = entries,
                            dateFormat = dateFormat,
                            targetWeightKg = targetWeightKg,
                            weightUnit = weightUnit,
                            palette = chartPalette,
                            onSelected = { selectedIndex = it },
                        )
                    }
                )
            }
        }
    }
}

private fun LineChart.configureWeightLineChart(
    entries: List<WeightEntry>,
    dateFormat: SimpleDateFormat,
    targetWeightKg: Double?,
    weightUnit: WeightUnit,
    palette: AppColorPalette,
    onSelected: (Int) -> Unit,
) {
    val lineEntries = entries.mapIndexed { index, entry ->
        Entry(index.toFloat(), weightUnit.toDisplay(entry.weightKg).toFloat())
    }
    val dataSet = LineDataSet(lineEntries, "体重").apply {
        color = palette.primary.toArgb()
        lineWidth = 2.4f
        setDrawCircles(false)
        valueTextSize = 0f
        setDrawValues(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        setDrawFilled(true)
        fillColor = palette.primary.toArgb()
        fillAlpha = 68
        highLightColor = palette.textPrimary.toArgb()
        setDrawHorizontalHighlightIndicator(false)
    }
    val extremes = WeightExtremes.from(entries)
    data = LineData(dataSet)
    description.isEnabled = false
    legend.isEnabled = false
    setNoDataText("还没有记录，想记的时候再记。")
    setNoDataTextColor(palette.textSecondary.toArgb())
    setBackgroundColor(android.graphics.Color.TRANSPARENT)
    setTouchEnabled(true)
    isDragEnabled = true
    isScaleXEnabled = true
    isScaleYEnabled = false
    setPinchZoom(true)
    isDoubleTapToZoomEnabled = true
    setDrawGridBackground(false)
    setExtraOffsets(4f, 6f, 12f, 8f)

    axisRight.isEnabled = false
    axisLeft.apply {
        removeAllLimitLines()
        textColor = palette.textSecondary.toArgb()
        gridColor = palette.border.toArgb()
        axisLineColor = android.graphics.Color.TRANSPARENT
        setDrawAxisLine(false)
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String = "%.1f".format(Locale.US, value)
        }
        extremes.highest?.let { entry ->
            addLimitLine(
                weightExtremeLimitLine(
                    label = "最高 ${formatWeight(entry.weightKg, weightUnit)}",
                    value = weightUnit.toDisplay(entry.weightKg).toFloat(),
                    color = AppColors.StatusMissed.toArgb(),
                )
            )
        }
        extremes.lowest?.let { entry ->
            addLimitLine(
                weightExtremeLimitLine(
                    label = "最低 ${formatWeight(entry.weightKg, weightUnit)}",
                    value = weightUnit.toDisplay(entry.weightKg).toFloat(),
                    color = AppColors.StatusKept.toArgb(),
                )
            )
        }
        targetWeightKg?.let { target ->
            addLimitLine(
                weightExtremeLimitLine(
                    label = "目标 ${formatWeight(target, weightUnit)}",
                    value = weightUnit.toDisplay(target).toFloat(),
                    color = palette.primary.toArgb(),
                )
            )
        }
    }
    xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        textColor = palette.textSecondary.toArgb()
        gridColor = android.graphics.Color.TRANSPARENT
        axisLineColor = android.graphics.Color.TRANSPARENT
        granularity = 1f
        setDrawLabels(false)
        setDrawGridLines(false)
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt().coerceIn(0, entries.lastIndex)
                return dateFormat.format(Date(entries[index].timestampMillis))
            }
        }
    }

    fitScreen()
    setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
        override fun onValueSelected(entry: Entry?, highlight: Highlight?) {
            entry?.x?.toInt()?.takeIf { it in entries.indices }?.let(onSelected)
        }

        override fun onNothingSelected() = Unit
    })
    invalidate()
}

private fun weightExtremeLimitLine(label: String, value: Float, color: Int): LimitLine {
    return LimitLine(value, label).apply {
        lineColor = color
        textColor = color
        lineWidth = 1.2f
        textSize = 10f
        enableDashedLine(10f, 8f, 0f)
        labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
    }
}

private fun LazyListScope.weightHistoryItems(
    items: List<WeightHistoryItem>,
    weightUnit: WeightUnit,
    onDeleteWeight: (WeightEntry) -> Unit,
) {
    if (items.isEmpty()) {
        item {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text("还没有记录。这个功能开着也可以先放着。", color = TextSecondary, fontSize = 13.sp)
            }
        }
    } else {
        items(items, key = { item: WeightHistoryItem ->
            when (item) {
                is WeightHistoryItem.MonthHeader -> "month-${item.month}"
                is WeightHistoryItem.EntryRow -> "entry-${item.entry.timestampMillis}"
            }
        }) { item: WeightHistoryItem ->
            when (item) {
                is WeightHistoryItem.MonthHeader -> WeightMonthHeader(month = item.month)
                is WeightHistoryItem.EntryRow -> WeightHistoryRow(
                    entry = item.entry,
                    emphasis = item.emphasis,
                    weightUnit = weightUnit,
                    onDelete = { onDeleteWeight(item.entry) },
                )
            }
        }
    }
}

@Composable
private fun WeightMonthHeader(month: YearMonth) {
    Text(
        text = "${month.year}年${month.monthValue}月",
        color = TextPrimary,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 12.dp, top = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeightHistoryRow(
    entry: WeightEntry,
    emphasis: WeightEmphasis,
    weightUnit: WeightUnit,
    onDelete: () -> Unit,
) {
    val format = remember { SimpleDateFormat("M月d日 HH:mm", Locale.CHINA) }
    var confirmDelete by remember(entry.timestampMillis) { mutableStateOf(false) }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                confirmDelete = true
            }
            false
        }
    )
    val rowColor = when (emphasis) {
        WeightEmphasis.Highest -> MissedSoft
        WeightEmphasis.Lowest -> SuccessSoft
        WeightEmphasis.None -> Neutral
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MissedSoft)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text("删除", color = Missed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(rowColor)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = format.format(Date(entry.timestampMillis)),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatWeight(entry.weightKg, weightUnit),
                    color = Primary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
                if (emphasis != WeightEmphasis.None) {
                    Text(
                        text = if (emphasis == WeightEmphasis.Highest) "最高" else "最低",
                        color = TextSecondary,
                        fontSize = 11.sp,
                    )
                }
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("删除这条体重？") },
            text = { Text("${format.format(Date(entry.timestampMillis))} · ${formatWeight(entry.weightKg, weightUnit)}") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmDelete = false
                        onDelete()
                    }
                ) {
                    Text("删除", color = Missed)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text("取消", color = TextSecondary)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthPickerSheet(
    groups: List<WeightMonthGroup>,
    onDismiss: () -> Unit,
    onSelectMonth: (YearMonth) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("定位月份", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("选择后会滚动到对应月份。", color = TextSecondary, fontSize = 13.sp)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(groups, key = { it.month.toString() }) { group ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Neutral)
                            .clickable { onSelectMonth(group.month) }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${group.month.year}年${group.month.monthValue}月",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Text("${group.entries.size}条", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TargetWeightSheet(
    currentTarget: Double?,
    weightUnit: WeightUnit,
    onDismiss: () -> Unit,
    onSave: (Double?) -> Unit,
) {
    var input by remember(currentTarget, weightUnit) {
        mutableStateOf(currentTarget?.let { "%.1f".format(Locale.US, weightUnit.toDisplay(it)) }.orEmpty())
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("设置目标体重", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("只在趋势图里画一条参考线，别的地方不提醒。", color = TextSecondary, fontSize = 13.sp)
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                placeholder = { Text(if (weightUnit == WeightUnit.JIN) "例如 116.0" else "例如 58.0") },
                trailingIcon = { Text(weightUnit.label, color = TextSecondary, fontWeight = FontWeight.SemiBold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { onSave(null) },
                    modifier = Modifier
                        .height(50.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                ) {
                    Text("清除")
                }
                Button(
                    onClick = {
                        val value = input.toDoubleOrNull()
                        val targetKg = value?.let(weightUnit::toKg)
                        if (targetKg != null && targetKg > 0.0 && targetKg < 500.0) {
                            onSave(targetKg)
                        }
                    },
                    modifier = Modifier
                        .height(52.dp)
                        .weight(1.3f),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("保存", fontWeight = FontWeight.Bold)
                }
            }
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("先不设", color = TextSecondary)
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

private sealed class WeightHistoryItem {
    data class MonthHeader(val month: YearMonth) : WeightHistoryItem()
    data class EntryRow(val entry: WeightEntry, val emphasis: WeightEmphasis) : WeightHistoryItem()
}

private enum class WeightEmphasis {
    None,
    Highest,
    Lowest,
}

private data class WeightExtremes(
    val highest: WeightEntry?,
    val lowest: WeightEntry?,
) {
    companion object {
        fun from(entries: List<WeightEntry>): WeightExtremes {
            return WeightExtremes(
                highest = entries.maxByOrNull { it.weightKg },
                lowest = entries.minByOrNull { it.weightKg },
            )
        }
    }
}

private data class WeightMonthGroup(
    val month: YearMonth,
    val entries: List<WeightEntry>,
)

private fun buildWeightMonthGroups(weights: List<WeightEntry>): List<WeightMonthGroup> {
    return WeightTrend.sorted(weights)
        .asReversed()
        .groupBy { entry ->
            YearMonth.from(
                Instant.ofEpochMilli(entry.timestampMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            )
        }
        .map { (month, entries) -> WeightMonthGroup(month = month, entries = entries) }
}

private fun buildWeightHistoryItems(
    groups: List<WeightMonthGroup>,
    extremes: WeightExtremes,
): List<WeightHistoryItem> {
    return groups.flatMap { group ->
        listOf(WeightHistoryItem.MonthHeader(group.month)) +
            group.entries.map { entry ->
                WeightHistoryItem.EntryRow(
                    entry = entry,
                    emphasis = when (entry.timestampMillis) {
                        extremes.highest?.timestampMillis -> WeightEmphasis.Highest
                        extremes.lowest?.timestampMillis -> WeightEmphasis.Lowest
                        else -> WeightEmphasis.None
                    }
                )
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsPage(
    settings: AppSettings,
    onSettingsChanged: (AppSettings) -> Unit,
    onPolicyRequested: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HeaderBlock("设置")
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Text("显示模式", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("晚上打卡可以切夜间，也可以跟随手机自动切换。", color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ThemeMode.values().forEach { mode ->
                    ModeButton(mode.label, settings.themeMode == mode) {
                        onSettingsChanged(settings.copy(themeMode = mode))
                    }
                }
            }
        }
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Text("日历显示模式", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ModeButton("月历", settings.calendarMode == CalendarMode.MONTH) {
                    onSettingsChanged(settings.copy(calendarMode = CalendarMode.MONTH))
                }
                ModeButton("周历", settings.calendarMode == CalendarMode.WEEK) {
                    onSettingsChanged(settings.copy(calendarMode = CalendarMode.WEEK))
                }
            }
        }
        FastingPlanSettingsCard(
            settings = settings,
            onSettingsChanged = onSettingsChanged,
        )
        SettingSwitch(
            title = "体重趋势",
            description = "开启后底部出现趋势页；默认隐藏。",
            checked = settings.weightTrendEnabled,
            onCheckedChange = { onSettingsChanged(settings.copy(weightTrendEnabled = it)) },
        )
        if (settings.weightTrendEnabled) {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("体重单位", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text("图表、记录和输入框会一起切换。", color = TextSecondary, fontSize = 13.sp)
                    }
                    WeightUnitToggle(
                        selectedUnit = settings.weightUnit,
                        onUnitSelected = { onSettingsChanged(settings.copy(weightUnit = it)) },
                        modifier = Modifier.height(44.dp),
                    )
                }
            }
        }
        SettingSwitch(
            title = "打卡后询问体重",
            description = "打卡后在同一个弹窗里补充，可跳过。",
            checked = settings.askWeightAfterCheckIn,
            onCheckedChange = { onSettingsChanged(settings.copy(askWeightAfterCheckIn = it)) },
        )
        SettingSwitch(
            title = "匿名使用统计",
            description = "帮助统计打开次数和功能点击，不上传体重、饮食内容或备注。",
            checked = settings.analyticsEnabled,
            onCheckedChange = {
                onSettingsChanged(
                    settings.copy(
                        privacyPolicyAccepted = true,
                        analyticsEnabled = it,
                    )
                )
            },
        )
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "查看隐私政策和用户协议" },
            onClick = onPolicyRequested,
        ) {
            Text("隐私政策 / 用户协议", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("查看本地数据、匿名统计和友盟 SDK 说明。", color = TextSecondary, fontSize = 13.sp)
        }
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "打开项目 GitHub 地址" },
            onClick = {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ProjectGitHubUrl)))
            },
        ) {
            Text("项目地址", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("github.com/KazeLiu/hold-that-bite", color = Primary, fontSize = 13.sp)
        }
    }
}

@Composable
private fun PrivacyConsentDialog(
    onEnableAnalytics: () -> Unit,
    onLocalOnly: () -> Unit,
    onShowPolicy: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text("隐私政策 / 用户协议", fontWeight = FontWeight.ExtraBold)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    "重点说明",
                    color = Primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                PrivacyHighlight("不收集精确位置，只允许城市级粗略统计")
                PrivacyHighlight("不读取应用列表")
                PrivacyHighlight("不上传体重数值、饮食内容或备注")
                Text(
                    "本 App 的打卡、体重和备注数据默认保存在你的设备本地。若你同意开启匿名使用统计，App 会使用友盟 SDK 做统计分析，用于了解打开次数和功能点击情况。",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
                Text(
                    "友盟 SDK 可能收集设备信息（Android ID、OAID、GUID 等）、网络信息、IP 地址和城市级粗略地域信息；本 App 不申请 GPS / 精确定位权限，不接入应用列表采集能力。",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
                TextButton(onClick = onShowPolicy) {
                    Text("查看完整说明", color = Primary, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onEnableAnalytics,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
            ) {
                Text("同意并开启匿名统计", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onLocalOnly) {
                Text("暂不开启，仅本地使用", color = TextSecondary)
            }
        },
    )
}

@Composable
private fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("隐私政策 / 用户协议", fontWeight = FontWeight.ExtraBold)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    "我们不会做这些",
                    color = Primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                PrivacyHighlight("不收集精确位置")
                PrivacyHighlight("不读取应用列表")
                PrivacyHighlight("不上传体重、饮食、备注等具体内容")
                PrivacySection(
                    title = "本地数据",
                    body = "打卡状态、拒绝零食次数、体重记录、目标体重和备注默认保存在设备本地，用于 App 自身展示。卸载 App 后，这些本地数据可能被系统清除。",
                )
                PrivacySection(
                    title = "匿名使用统计",
                    body = "开启后仅统计 App 打开、守住了、没守住、拒绝零食、体重趋势开关、趋势页访问、记录体重、打开设置等功能动作，不上报动作里的具体个人内容。",
                )
                PrivacySection(
                    title = "第三方 SDK",
                    body = "使用 SDK 名称：友盟 SDK。服务类型：统计分析。可能收集个人信息类型：设备信息（Android ID、OAID、GUID 等）、网络信息、IP 地址、城市级粗略地域信息。本 App 不申请 GPS / 精确定位权限，不接入应用列表采集能力。",
                )
                TextButton(
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(UmengPrivacyPolicyUrl)))
                    },
                ) {
                    Text("查看友盟隐私政策", color = Primary, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
            ) {
                Text("知道了", fontWeight = FontWeight.Bold)
            }
        },
    )
}

@Composable
private fun PrivacyHighlight(text: String) {
    Text(
        text = text,
        color = Missed,
        fontSize = 17.sp,
        fontWeight = FontWeight.ExtraBold,
        lineHeight = 23.sp,
    )
}

@Composable
private fun PrivacySection(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(body, color = TextSecondary, fontSize = 14.sp, lineHeight = 20.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FastingPlanSettingsCard(settings: AppSettings, onSettingsChanged: (AppSettings) -> Unit) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val firstMeal = MealTime(hour = settings.firstMealHour, minute = settings.firstMealMinute)
    val lastBite = settings.fastingPlan.lastBiteTime(firstMeal)

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text("减肥安排", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("选好进食窗口，再定第一餐时间。", color = TextSecondary, fontSize = 13.sp)
        Spacer(Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = settings.fastingPlan.label,
                onValueChange = {},
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                label = { Text("当前安排") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(18.dp),
                singleLine = true,
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                FastingPlan.values().forEach { plan ->
                    DropdownMenuItem(
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(plan.label, color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text(plan.description, color = TextSecondary, fontSize = 13.sp)
                            }
                        },
                        onClick = {
                            expanded = false
                            onSettingsChanged(settings.copy(fastingPlan = plan))
                        },
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(settings.fastingPlan.description, color = TextSecondary, fontSize = 13.sp)

        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("第一餐时间", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(3.dp))
                Text("默认上午 09:00，可按你的作息调整。", color = TextSecondary, fontSize = 13.sp)
            }
            OutlinedButton(
                onClick = {
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            onSettingsChanged(
                                settings.copy(
                                    firstMealHour = hour,
                                    firstMealMinute = minute,
                                )
                            )
                        },
                        settings.firstMealHour,
                        settings.firstMealMinute,
                        true,
                    ).show()
                },
                modifier = Modifier
                    .height(48.dp)
                    .width(104.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
            ) {
                Text(firstMeal.displayText, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Neutral)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("最后一口时间", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(3.dp))
                Text("按 ${settings.fastingPlan.label} 自动计算。", color = TextSecondary, fontSize = 13.sp)
            }
            Text(lastBite.displayText, color = Primary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun ModeButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Primary.copy(alpha = 0.14f) else Neutral)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = if (selected) Primary else TextPrimary, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun WeightUnitToggle(
    selectedUnit: WeightUnit,
    onUnitSelected: (WeightUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .width(92.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Neutral)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WeightUnit.values().forEach { unit ->
            val selected = unit == selectedUnit
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(13.dp))
                    .background(if (selected) Primary.copy(alpha = 0.14f) else Color.Transparent)
                    .clickable { onUnitSelected(unit) }
                    .semantics { contentDescription = "显示${unit.label}" },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = unit.label,
                    color = if (selected) Primary else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun SettingSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(description, color = TextSecondary, fontSize = 13.sp)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun AppBottomNav(
    visibleTabs: List<AppTab>,
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabCount = visibleTabs.size.coerceAtLeast(1)
    val selectedIndex = visibleTabs.indexOf(currentTab).takeIf { it >= 0 } ?: 0
    val motionSpec = tween<androidx.compose.ui.unit.Dp>(
        durationMillis = TabTransitionMillis,
        easing = FastOutSlowInEasing
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceColor)
            .padding(horizontal = 34.dp, vertical = 4.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            val indicatorWidth = 54.dp
            val indicatorHeight = 44.dp
            val slotWidth = maxWidth / tabCount.toFloat()
            val indicatorOffset by animateDpAsState(
                targetValue = (slotWidth * selectedIndex.toFloat()) + ((slotWidth - indicatorWidth) / 2f),
                animationSpec = motionSpec,
                label = "bottom-nav-indicator-offset",
            )

            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset, y = 2.dp)
                    .size(width = indicatorWidth, height = indicatorHeight)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Primary.copy(alpha = 0.12f))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                visibleTabs.forEach { tab ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        NavIconButton(
                            tab = tab,
                            selected = tab == currentTab,
                            onClick = { onTabSelected(tab) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NavIconButton(tab: AppTab, selected: Boolean, onClick: () -> Unit) {
    val color by animateColorAsState(
        targetValue = if (selected) Primary else TextSecondary,
        animationSpec = tween(durationMillis = TabTransitionMillis, easing = FastOutSlowInEasing),
        label = "bottom-nav-icon-color",
    )
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = tween(durationMillis = TabTransitionMillis, easing = FastOutSlowInEasing),
        label = "bottom-nav-icon-scale",
    )

    Box(
        modifier = Modifier
            .size(width = 54.dp, height = 44.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .semantics { contentDescription = tab.label },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = when (tab) {
                AppTab.TREND -> Icons.Filled.TrendingUp
                AppTab.HOME -> Icons.Filled.Home
                AppTab.SETTINGS -> Icons.Filled.Settings
            },
            contentDescription = tab.label,
            tint = color,
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                }
        )
    }
}

@Composable
private fun HeaderBlock(title: String) {
    Column {
        Text(title, color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)
    val colors = CardDefaults.cardColors(containerColor = SurfaceColor)
    val elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    val cardContent: @Composable () -> Unit = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(SurfaceColor, SurfaceSubtle)
                    )
                )
                .padding(14.dp),
            content = content
        )
    }

    if (onClick == null) {
        Card(
            modifier = modifier,
            shape = shape,
            colors = colors,
            elevation = elevation,
            content = { cardContent() },
        )
    } else {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = colors,
            elevation = elevation,
            content = { cardContent() },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheckInSupplementSheet(
    initialNote: String,
    includeWeight: Boolean,
    weightUnit: WeightUnit,
    onDismiss: () -> Unit,
    onDone: (CheckInSupplement) -> Unit,
) {
    var note by remember(initialNote) { mutableStateOf(initialNote) }
    var weight by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("打卡已记录", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("想补一句就写，不写也不影响今天。", color = TextSecondary, fontSize = 14.sp)
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp),
                shape = RoundedCornerShape(18.dp),
                placeholder = { Text("备注，可跳过") },
                maxLines = 3,
            )

            if (includeWeight) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    placeholder = {
                        Text(if (weightUnit == WeightUnit.JIN) "体重，例如 125，可跳过" else "体重，例如 62.5，可跳过")
                    },
                    trailingIcon = { Text(weightUnit.label, color = TextSecondary, fontWeight = FontWeight.SemiBold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .height(44.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                ) {
                    Text("跳过")
                }
                Button(
                    onClick = {
                        onDone(
                            CheckInSupplement.from(
                                note = note,
                                weight = if (includeWeight) weight else null,
                                weightUnit = weightUnit,
                            )
                        )
                    },
                    modifier = Modifier
                        .height(44.dp)
                        .weight(1.3f),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("好了", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeightSheet(
    weightUnit: WeightUnit,
    onDismiss: () -> Unit,
    onSave: (WeightEntry?) -> Unit,
) {
    val context = LocalContext.current
    var input by remember { mutableStateOf("") }
    var timestampMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    val dateFormat = remember { SimpleDateFormat("yyyy年M月d日", Locale.CHINA) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.CHINA) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("记录体重", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("选好时间，记下这一刻就好。", color = TextSecondary, fontSize = 14.sp)
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                placeholder = { Text(if (weightUnit == WeightUnit.JIN) "例如 125" else "例如 62.5") },
                trailingIcon = { Text(weightUnit.label, color = TextSecondary, fontWeight = FontWeight.SemiBold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance().apply { timeInMillis = timestampMillis }
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                val updated = Calendar.getInstance().apply {
                                    timeInMillis = timestampMillis
                                    set(Calendar.YEAR, year)
                                    set(Calendar.MONTH, month)
                                    set(Calendar.DAY_OF_MONTH, day)
                                }
                                timestampMillis = updated.timeInMillis
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH),
                        ).show()
                    },
                    modifier = Modifier
                        .height(44.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) {
                    Text(dateFormat.format(Date(timestampMillis)), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance().apply { timeInMillis = timestampMillis }
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                val updated = Calendar.getInstance().apply {
                                    timeInMillis = timestampMillis
                                    set(Calendar.HOUR_OF_DAY, hour)
                                    set(Calendar.MINUTE, minute)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                timestampMillis = updated.timeInMillis
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true,
                        ).show()
                    },
                    modifier = Modifier
                        .height(44.dp)
                        .width(96.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) {
                    Text(timeFormat.format(Date(timestampMillis)))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .height(44.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                ) {
                    Text("先不记")
                }
                Button(
                    onClick = {
                        onSave(input.toDoubleOrNull()?.let { WeightEntry(timestampMillis, weightUnit.toKg(it)) })
                    },
                    modifier = Modifier
                        .height(44.dp)
                        .weight(1.3f),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("记录", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

private fun clampDateToMonth(current: LocalDate, target: YearMonth): LocalDate {
    return target.atDay(min(current.dayOfMonth, target.lengthOfMonth()))
}

private fun formatWeekTitle(date: LocalDate): String {
    val weekFields = WeekFields.ISO
    val weekYear = date.get(weekFields.weekBasedYear())
    val weekNumber = date.get(weekFields.weekOfWeekBasedYear())
    return "${weekYear}年第${weekNumber}周"
}
