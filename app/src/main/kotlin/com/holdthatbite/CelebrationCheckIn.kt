package com.holdthatbite

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Undo
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.holdthatbite.domain.Motivation
import com.holdthatbite.domain.WeightUnit
import com.holdthatbite.ui.AppColors
import com.holdthatbite.ui.CheckInSupplement
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val CelebrationLogTag = "HTB-Celebration"
private const val KeptActionDelayMillis = 360L
private const val KeptLongPressStartMillis = 260L
private const val KeptLongPressIntervalMillis = 30L
private const val VictoryCardIntroMillis = 360

@Composable
internal fun CelebrationCheckInActions(
    onMissed: () -> Unit,
    onKept: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val emojiEffectState = rememberButtonEmojiEffectState(logTag = CelebrationLogTag)
    var keptPressing by remember { mutableStateOf(false) }
    var keptActionPending by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        val gap = 10.dp
        val totalWeight = 2.45f
        val contentWidth = maxWidth - gap
        val missedWidth = contentWidth * (1f / totalWeight)
        val keptWidth = contentWidth * (1.45f / totalWeight)
        val keptLeftX = missedWidth + gap
        val keptOriginX = missedWidth + gap + keptWidth / 2f
        val keptOriginY = 28.dp

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(gap),
        ) {
            OutlinedButton(
                onClick = onMissed,
                modifier = Modifier
                    .height(54.dp)
                    .weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.StatusMissed)
            ) {
                Text("🍜 没守住", fontWeight = FontWeight.SemiBold)
            }
            KeptCelebrationButton(
                pressing = keptPressing,
                actionPending = keptActionPending,
                onPressingChanged = { keptPressing = it },
                onActionPendingChanged = { keptActionPending = it },
                onTapBurst = {
                    emojiEffectState.spawnParticles(
                        count = 34,
                        style = ButtonEmojiParticleStyle.KEPT_TAP,
                        originX = keptOriginX.value,
                        originY = keptOriginY.value,
                    )
                },
                onLongPressBurst = { localX, localY ->
                    emojiEffectState.spawnParticles(
                        count = 1,
                        style = ButtonEmojiParticleStyle.KEPT_STREAM,
                        originX = keptLeftX.value + localX,
                        originY = localY,
                    )
                },
                onKept = {
                    Log.d(CelebrationLogTag, "show victory card with activeParticles=${emojiEffectState.activeParticleCount}")
                    onKept()
                },
                modifier = Modifier
                    .height(56.dp)
                    .weight(1.45f),
            )
        }

        ButtonEmojiEffectLayer(effectState = emojiEffectState)
    }
}

@Composable
private fun KeptCelebrationButton(
    pressing: Boolean,
    actionPending: Boolean,
    onPressingChanged: (Boolean) -> Unit,
    onActionPendingChanged: (Boolean) -> Unit,
    onTapBurst: () -> Unit,
    onLongPressBurst: (Float, Float) -> Unit,
    onKept: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val currentActionPending by rememberUpdatedState(actionPending)
    val currentOnPressingChanged by rememberUpdatedState(onPressingChanged)
    val currentOnActionPendingChanged by rememberUpdatedState(onActionPendingChanged)
    val currentOnTapBurst by rememberUpdatedState(onTapBurst)
    val currentOnLongPressBurst by rememberUpdatedState(onLongPressBurst)
    val currentOnKept by rememberUpdatedState(onKept)
    val pressScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (pressing) 0.965f else 1f,
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
        label = "kept-button-press-scale",
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.ThemeBlue)
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var pressOriginX = with(density) { down.position.x.toDp().value }
                    var pressOriginY = with(density) { down.position.y.toDp().value }
                    Log.d(CelebrationLogTag, "down actionPending=$currentActionPending")
                    if (currentActionPending) {
                        waitUntilReleased()
                        Log.d(CelebrationLogTag, "release ignored while action pending")
                        return@awaitEachGesture
                    }

                    currentOnPressingChanged(true)
                    var longPressStarted = false
                    var longPressTicks = 0
                    val longPressJob = scope.launch {
                        delay(KeptLongPressStartMillis)
                        longPressStarted = true
                        Log.d(CelebrationLogTag, "long preview start")
                        while (true) {
                            currentOnLongPressBurst(pressOriginX, pressOriginY)
                            longPressTicks += 1
                            delay(KeptLongPressIntervalMillis)
                        }
                    }

                    try {
                        val releasedInside = waitUntilReleasedOrOutside(down.id) { position ->
                            pressOriginX = with(density) { position.x.toDp().value }
                            pressOriginY = with(density) { position.y.toDp().value }
                        }
                        if (!releasedInside) {
                            Log.d(CelebrationLogTag, "release simulated by leaving button bounds")
                        }
                    } finally {
                        longPressJob.cancel()
                        currentOnPressingChanged(false)
                    }

                    if (longPressStarted) {
                        Log.d(CelebrationLogTag, "long preview release ticks=$longPressTicks no action")
                    } else if (pressOriginX >= 0f && pressOriginY >= 0f) {
                        Log.d(CelebrationLogTag, "tap release -> single burst and delayed action")
                        currentOnActionPendingChanged(true)
                        currentOnTapBurst()
                        scope.launch {
                            delay(KeptActionDelayMillis)
                            Log.d(CelebrationLogTag, "tap action commit")
                            currentOnKept()
                            currentOnActionPendingChanged(false)
                        }
                    } else {
                        Log.d(CelebrationLogTag, "tap canceled outside button bounds")
                    }
                }
            }
            .semantics { contentDescription = "守住了" },
        contentAlignment = Alignment.Center,
    ) {
        Text("🛡️ 守住了", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun SnackRefusalAction(
    count: Int,
    onAdd: () -> Unit,
    onUndo: () -> Unit,
) {
    val emojiEffectState = rememberButtonEmojiEffectState(logTag = CelebrationLogTag)

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val buttonOriginX = maxWidth * 0.64f
        val buttonOriginY = 26.dp

        fun spawnSnackParticles() {
            emojiEffectState.spawnParticles(
                count = 9,
                style = ButtonEmojiParticleStyle.SNACK_TAP,
                originX = buttonOriginX.value,
                originY = buttonOriginY.value,
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SnackRefusalCountLabel(
                    count = count,
                    modifier = Modifier.weight(1f),
                )
                OutlinedButton(
                    onClick = {
                        spawnSnackParticles()
                        onAdd()
                    },
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.ThemeBlue)
                ) {
                    Text("🚫 拒绝零食 +1", fontWeight = FontWeight.SemiBold, maxLines = 1)
                }
                if (count > 0) {
                    IconButton(
                        onClick = onUndo,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Undo,
                            contentDescription = "撤销一次拒绝零食",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        ButtonEmojiEffectLayer(effectState = emojiEffectState)
    }
}

@Composable
private fun SnackRefusalCountLabel(
    count: Int,
    modifier: Modifier = Modifier,
) {
    val encouragement = Motivation.snackRefusalEncouragement(count)
    if (count <= 0) {
        Text(
            "还没小胜利",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier,
        )
        return
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${encouragement?.shortLabel ?: "小胜利"} ",
                color = AppColors.StatusKept,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            AnimatedContent(
                targetState = count,
                transitionSpec = {
                    val increasing = targetState > initialState
                    val enterOffset: (Int) -> Int = { height -> if (increasing) height else -height }
                    val exitOffset: (Int) -> Int = { height -> if (increasing) -height else height }

                    (slideInVertically(animationSpec = tween(220), initialOffsetY = enterOffset) +
                        fadeIn(animationSpec = tween(160))) togetherWith
                        (slideOutVertically(animationSpec = tween(220), targetOffsetY = exitOffset) +
                            fadeOut(animationSpec = tween(140)))
                },
                label = "snack-refusal-count-roll",
            ) { animatedCount ->
                Text(
                    animatedCount.toString(),
                    color = AppColors.StatusKept,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    modifier = Modifier.graphicsLayer { translationY = 2f },
                )
            }
            Text(
                " 次",
                color = AppColors.StatusKept,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
        AnimatedContent(
            targetState = encouragement?.detail.orEmpty(),
            transitionSpec = {
                (fadeIn(animationSpec = tween(180)) +
                    slideInVertically(animationSpec = tween(220), initialOffsetY = { it / 2 })) togetherWith
                    (fadeOut(animationSpec = tween(120)) +
                        slideOutVertically(animationSpec = tween(160), targetOffsetY = { -it / 2 }))
            },
            label = "snack-refusal-encouragement-detail",
        ) { detail ->
            Text(
                detail,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun VictoryCheckInSupplementDialog(
    initialNote: String,
    includeWeight: Boolean,
    weightUnit: WeightUnit,
    onDismiss: () -> Unit,
    onDone: (CheckInSupplement) -> Unit,
) {
    var note by remember(initialNote) { mutableStateOf(initialNote) }
    var weight by remember { mutableStateOf("") }
    val intro = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        intro.snapTo(0f)
        intro.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = VictoryCardIntroMillis, easing = FastOutSlowInEasing),
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.52f))
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 24.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            VictorySupplementCard(
                progress = intro.value,
                note = note,
                includeWeight = includeWeight,
                weightUnit = weightUnit,
                weight = weight,
                onNoteChanged = { note = it },
                onWeightChanged = { weight = it },
                onDismiss = onDismiss,
                onDone = {
                    onDone(
                        CheckInSupplement.from(
                            note = note,
                            weight = if (includeWeight) weight else null,
                            weightUnit = weightUnit,
                        )
                    )
                },
            )
        }
    }
}

@Composable
private fun VictorySupplementCard(
    progress: Float,
    note: String,
    includeWeight: Boolean,
    weightUnit: WeightUnit,
    weight: String,
    onNoteChanged: (String) -> Unit,
    onWeightChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onDone: () -> Unit,
) {
    val density = LocalDensity.current
    val scale = 0.12f + (0.88f * progress)
    val cardAlpha = (progress / 0.1f).coerceIn(0f, 1f)
    val victoryTint = AppColors.StatusKept.copy(alpha = 0.22f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 58.dp)
            .graphicsLayer {
                alpha = cardAlpha
                rotationY = 180f * (1f - progress)
                scaleX = scale
                scaleY = scale
                cameraDistance = 12f * density.density
            },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 18.dp),
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            victoryTint,
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surface,
                        )
                    )
                )
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp),
                contentAlignment = Alignment.Center,
            ) {
                TitleConfettiRain(visibleAlpha = (progress - 0.72f).coerceIn(0f, 1f) / 0.28f)
                Text(
                    text = "守住了",
                    color = AppColors.ThemeBlue,
                    fontSize = 42.sp,
                    lineHeight = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        shadow = Shadow(
                            color = AppColors.ThemeBlue.copy(alpha = 0.36f),
                            offset = Offset(x = 0f, y = 8f),
                            blurRadius = 24f,
                        )
                    ),
                )
            }
            Text(
                text = "漂亮！这一口你赢下来了，把今天的小胜利记下来。",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
            )
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChanged,
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
                    onValueChange = onWeightChanged,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    placeholder = {
                        Text(if (weightUnit == WeightUnit.JIN) "体重，例如 125，可跳过" else "体重，例如 62.5，可跳过")
                    },
                    trailingIcon = {
                        Text(weightUnit.label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .height(46.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                ) {
                    Text("先跳过")
                }
                Button(
                    onClick = onDone,
                    modifier = Modifier
                        .height(46.dp)
                        .weight(1.3f),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.ThemeBlue)
                ) {
                    Text("记下这次", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(2.dp))
        }
    }
}

private suspend fun androidx.compose.ui.input.pointer.AwaitPointerEventScope.waitUntilReleased() {
    do {
        val event = awaitPointerEvent()
    } while (event.changes.any { it.pressed })
}

private suspend fun androidx.compose.ui.input.pointer.AwaitPointerEventScope.waitUntilReleasedOrOutside(
    pointerId: PointerId,
    onPositionChanged: (Offset) -> Unit,
): Boolean {
    while (true) {
        val event = awaitPointerEvent()
        val change = event.changes.firstOrNull { it.id == pointerId } ?: return false
        if (!change.pressed) {
            return true
        }

        val position = change.position
        onPositionChanged(position)
        if (position.x < 0f || position.y < 0f || position.x > size.width || position.y > size.height) {
            onPositionChanged(Offset(x = -1f, y = -1f))
            return false
        }
    }
}
