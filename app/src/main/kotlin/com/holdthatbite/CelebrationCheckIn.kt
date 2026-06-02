package com.holdthatbite

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.holdthatbite.domain.WeightUnit
import com.holdthatbite.ui.AppColors
import com.holdthatbite.ui.CheckInSupplement
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val CelebrationLogTag = "HTB-Celebration"
private const val KeptActionDelayMillis = 360L
private const val KeptLongPressStartMillis = 260L
private const val KeptLongPressIntervalMillis = 30L
private const val VictoryCardIntroMillis = 360
private const val MaxStreamParticles = 96

private enum class ButtonEmojiParticleStyle {
    KEPT_TAP,
    KEPT_STREAM,
    SNACK_TAP
}

private data class ButtonEmojiParticle(
    val id: Long,
    val emoji: String,
    val originX: Float,
    val originY: Float,
    val dx: Float,
    val dy: Float,
    val rotation: Float,
    val scale: Float,
    val sizeSp: Int,
    val durationMillis: Int,
    val fadeStart: Float,
)

private data class TitleConfettiSpec(
    val xFraction: Float,
    val width: Dp,
    val height: Dp,
    val color: Color,
    val drift: Dp,
    val rotation: Float,
    val phase: Float,
    val durationMillis: Int,
)

@Composable
internal fun CelebrationCheckInActions(
    onMissed: () -> Unit,
    onKept: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val particles = remember { mutableStateListOf<ButtonEmojiParticle>() }
    var nextParticleId by remember { mutableLongStateOf(0L) }
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

        fun spawnParticles(count: Int, style: ButtonEmojiParticleStyle, originX: Float = keptOriginX.value, originY: Float = keptOriginY.value) {
            if (style != ButtonEmojiParticleStyle.KEPT_STREAM) {
                Log.d(CelebrationLogTag, "spawn tap count=$count activeBefore=${particles.size}")
            }
            if (style == ButtonEmojiParticleStyle.KEPT_STREAM && particles.size >= MaxStreamParticles) {
                Log.d(CelebrationLogTag, "stream skip active=${particles.size}")
                return
            }
            if (style != ButtonEmojiParticleStyle.KEPT_STREAM) {
                particles.clear()
            }
            repeat(count) {
                particles.add(
                    buildButtonEmojiParticle(
                        id = nextParticleId++,
                        originX = originX,
                        originY = originY,
                        style = style,
                    )
                )
            }
        }

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
                onTapBurst = { spawnParticles(34, ButtonEmojiParticleStyle.KEPT_TAP) },
                onLongPressBurst = { localX, localY ->
                    spawnParticles(
                        count = 1,
                        style = ButtonEmojiParticleStyle.KEPT_STREAM,
                        originX = keptLeftX.value + localX,
                        originY = localY,
                    )
                },
                onKept = {
                    Log.d(CelebrationLogTag, "show victory card with activeParticles=${particles.size}")
                    onKept()
                },
                modifier = Modifier
                    .height(56.dp)
                    .weight(1.45f),
            )
        }

        particles.forEach { particle ->
            key(particle.id) {
                ButtonEmojiParticleView(
                    particle = particle,
                    onFinished = { finishedId ->
                        particles.removeAll { it.id == finishedId }
                    },
                )
            }
        }
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
            .pointerInput(actionPending, onKept) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var pressOriginX = with(density) { down.position.x.toDp().value }
                    var pressOriginY = with(density) { down.position.y.toDp().value }
                    Log.d(CelebrationLogTag, "down actionPending=$actionPending")
                    if (actionPending) {
                        waitUntilReleased()
                        Log.d(CelebrationLogTag, "release ignored while action pending")
                        return@awaitEachGesture
                    }

                    onPressingChanged(true)
                    var longPressStarted = false
                    var longPressTicks = 0
                    val longPressJob = scope.launch {
                        delay(KeptLongPressStartMillis)
                        longPressStarted = true
                        Log.d(CelebrationLogTag, "long preview start")
                        while (true) {
                            onLongPressBurst(pressOriginX, pressOriginY)
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
                        onPressingChanged(false)
                    }

                    if (longPressStarted) {
                        Log.d(CelebrationLogTag, "long preview release ticks=$longPressTicks no action")
                    } else if (pressOriginX >= 0f && pressOriginY >= 0f) {
                        Log.d(CelebrationLogTag, "tap release -> single burst and delayed action")
                        onActionPendingChanged(true)
                        onTapBurst()
                        scope.launch {
                            delay(KeptActionDelayMillis)
                            Log.d(CelebrationLogTag, "tap action commit")
                            onKept()
                            onActionPendingChanged(false)
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
private fun ButtonEmojiParticleView(
    particle: ButtonEmojiParticle,
    onFinished: (Long) -> Unit,
) {
    val progress = remember(particle.id) { Animatable(0f) }

    LaunchedEffect(particle.id) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = particle.durationMillis, easing = FastOutSlowInEasing),
        )
        onFinished(particle.id)
    }

    val value = progress.value
    val alpha = when {
        value < 0.12f -> value / 0.12f
        value > particle.fadeStart -> ((1f - value) / (1f - particle.fadeStart)).coerceIn(0f, 1f)
        else -> 1f
    }
    Text(
        text = particle.emoji,
        fontSize = particle.sizeSp.sp,
        modifier = Modifier
            .offset(
                x = (particle.originX + particle.dx * value).dp - 17.dp,
                y = (particle.originY + particle.dy * value).dp - 17.dp,
            )
            .graphicsLayer {
                this.alpha = alpha
                scaleX = 0.48f + (particle.scale - 0.48f) * value
                scaleY = 0.48f + (particle.scale - 0.48f) * value
                rotationZ = particle.rotation * value
            }
    )
}

@Composable
internal fun SnackRefusalAction(
    count: Int,
    onAdd: () -> Unit,
    onUndo: () -> Unit,
) {
    val particles = remember { mutableStateListOf<ButtonEmojiParticle>() }
    var nextParticleId by remember { mutableLongStateOf(0L) }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val buttonOriginX = maxWidth * 0.64f
        val buttonOriginY = 26.dp

        fun spawnSnackParticles() {
            particles.clear()
            repeat(9) {
                particles.add(
                    buildButtonEmojiParticle(
                        id = nextParticleId++,
                        originX = buttonOriginX.value,
                        originY = buttonOriginY.value,
                        style = ButtonEmojiParticleStyle.SNACK_TAP,
                    )
                )
            }
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
                Text(
                    if (count > 0) "小胜利 $count 次" else "还没小胜利",
                    color = if (count > 0) AppColors.StatusKept else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
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

        particles.forEach { particle ->
            key(particle.id) {
                ButtonEmojiParticleView(
                    particle = particle,
                    onFinished = { finishedId ->
                        particles.removeAll { it.id == finishedId }
                    },
                )
            }
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

@Composable
private fun TitleConfettiRain(visibleAlpha: Float) {
    val specs = remember {
        List(26) {
            TitleConfettiSpec(
                xFraction = Random.nextFloat(),
                width = (16 + Random.nextInt(22)).dp,
                height = (5 + Random.nextInt(4)).dp,
                color = listOf(
                    AppColors.ThemeBlue,
                    AppColors.CelebrationGold,
                    AppColors.CelebrationPink,
                    AppColors.CelebrationMint,
                    Color.White,
                    AppColors.CelebrationSky,
                ).random(),
                drift = (-24 + Random.nextInt(49)).dp,
                rotation = Random.nextFloat() * 180f,
                phase = Random.nextFloat(),
                durationMillis = 1700 + Random.nextInt(1100),
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "victory-title-confetti")
    val tick by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "victory-title-confetti-tick",
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        specs.forEach { spec ->
            val localProgress = (tick + spec.phase) % 1f
            val fadeIn = (localProgress / 0.16f).coerceIn(0f, 1f)
            val fadeOut = ((1f - localProgress) / 0.22f).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .offset(
                        x = (maxWidth * spec.xFraction) + (spec.drift * localProgress),
                        y = (-22).dp + (128.dp * localProgress),
                    )
                    .size(width = spec.width, height = spec.height)
                    .graphicsLayer {
                        alpha = visibleAlpha * fadeIn * fadeOut
                        rotationZ = spec.rotation + 260f * localProgress
                    }
                    .clip(RoundedCornerShape(99.dp))
                    .background(spec.color)
            )
        }
    }
}

private fun buildButtonEmojiParticle(
    id: Long,
    originX: Float,
    originY: Float,
    style: ButtonEmojiParticleStyle,
): ButtonEmojiParticle {
    val emojis = when (style) {
        ButtonEmojiParticleStyle.KEPT_TAP -> listOf("🎉", "✨", "🌟", "💪", "🥳", "🔥", "💙", "⭐", "🚀", "🏆", "🛡️", "💥")
        ButtonEmojiParticleStyle.KEPT_STREAM -> listOf("🎉", "✨", "🌟", "💪", "🥳", "🔥", "💙", "⭐")
        ButtonEmojiParticleStyle.SNACK_TAP -> listOf("✨", "⭐", "💪", "🍵", "🚫", "💙")
    }
    val angleDegrees = when (style) {
        ButtonEmojiParticleStyle.KEPT_TAP -> Random.nextFloat() * 360f
        ButtonEmojiParticleStyle.KEPT_STREAM -> -90f + Random.nextFloat() * 96f - 48f
        ButtonEmojiParticleStyle.SNACK_TAP -> -90f + Random.nextFloat() * 130f - 65f
    }
    val angleRadians = Math.toRadians(angleDegrees.toDouble())
    val distance = when (style) {
        ButtonEmojiParticleStyle.KEPT_TAP -> 124f + Random.nextFloat() * 224f
        ButtonEmojiParticleStyle.KEPT_STREAM -> 96f + Random.nextFloat() * 124f
        ButtonEmojiParticleStyle.SNACK_TAP -> 46f + Random.nextFloat() * 94f
    }
    val liftBias = when (style) {
        ButtonEmojiParticleStyle.KEPT_TAP -> -28f - Random.nextFloat() * 52f
        ButtonEmojiParticleStyle.KEPT_STREAM -> -48f - Random.nextFloat() * 50f
        ButtonEmojiParticleStyle.SNACK_TAP -> -24f - Random.nextFloat() * 36f
    }
    val rotationDirection = if (Random.nextBoolean()) 1f else -1f
    return ButtonEmojiParticle(
        id = id,
        emoji = emojis.random(),
        originX = originX + Random.nextFloat() * 18f - 9f,
        originY = originY + Random.nextFloat() * 14f - 7f,
        dx = kotlin.math.cos(angleRadians).toFloat() * distance,
        dy = kotlin.math.sin(angleRadians).toFloat() * distance + liftBias,
        rotation = rotationDirection * when (style) {
            ButtonEmojiParticleStyle.KEPT_TAP -> 140f + Random.nextFloat() * 320f
            ButtonEmojiParticleStyle.KEPT_STREAM -> 80f + Random.nextFloat() * 230f
            ButtonEmojiParticleStyle.SNACK_TAP -> 50f + Random.nextFloat() * 160f
        },
        scale = when (style) {
            ButtonEmojiParticleStyle.KEPT_TAP -> 0.82f + Random.nextFloat() * 0.82f
            ButtonEmojiParticleStyle.KEPT_STREAM -> 0.72f + Random.nextFloat() * 0.72f
            ButtonEmojiParticleStyle.SNACK_TAP -> 0.62f + Random.nextFloat() * 0.48f
        },
        sizeSp = when (style) {
            ButtonEmojiParticleStyle.KEPT_TAP -> 21 + Random.nextInt(16)
            ButtonEmojiParticleStyle.KEPT_STREAM -> 19 + Random.nextInt(10)
            ButtonEmojiParticleStyle.SNACK_TAP -> 18 + Random.nextInt(8)
        },
        durationMillis = when (style) {
            ButtonEmojiParticleStyle.KEPT_TAP -> 620 + Random.nextInt(220)
            ButtonEmojiParticleStyle.KEPT_STREAM -> 980 + Random.nextInt(420)
            ButtonEmojiParticleStyle.SNACK_TAP -> 520 + Random.nextInt(160)
        },
        fadeStart = when (style) {
            ButtonEmojiParticleStyle.KEPT_STREAM -> 0.36f
            ButtonEmojiParticleStyle.SNACK_TAP -> 0.66f
            ButtonEmojiParticleStyle.KEPT_TAP -> 0.72f
        },
    )
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
