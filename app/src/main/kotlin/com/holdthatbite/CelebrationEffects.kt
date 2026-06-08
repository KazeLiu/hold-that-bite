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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holdthatbite.ui.AppColors
import kotlin.random.Random

private const val DefaultMaxStreamParticles = 96

internal enum class ButtonEmojiParticleStyle {
    KEPT_TAP,
    KEPT_STREAM,
    SNACK_TAP
}

internal data class ButtonEmojiParticle(
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
)

internal class ButtonEmojiEffectState(
    private val logTag: String,
    private val maxStreamParticles: Int = DefaultMaxStreamParticles,
) {
    private val activeParticles = mutableStateListOf<ButtonEmojiParticle>()
    private var nextParticleId = 0L

    val particles: List<ButtonEmojiParticle>
        get() = activeParticles

    val activeParticleCount: Int
        get() = activeParticles.size

    fun spawnParticles(
        count: Int,
        style: ButtonEmojiParticleStyle,
        originX: Float,
        originY: Float,
    ) {
        if (style != ButtonEmojiParticleStyle.KEPT_STREAM) {
            Log.d(logTag, "spawn tap count=$count activeBefore=${activeParticles.size}")
        }
        if (style == ButtonEmojiParticleStyle.KEPT_STREAM && activeParticles.size >= maxStreamParticles) {
            Log.d(logTag, "stream skip active=${activeParticles.size}")
            return
        }
        if (style != ButtonEmojiParticleStyle.KEPT_STREAM) {
            activeParticles.clear()
        }
        repeat(count) {
            activeParticles.add(
                buildButtonEmojiParticle(
                    id = nextParticleId++,
                    originX = originX,
                    originY = originY,
                    style = style,
                )
            )
        }
    }

    fun removeParticle(id: Long) {
        activeParticles.removeAll { it.id == id }
    }
}

@Composable
internal fun rememberButtonEmojiEffectState(
    logTag: String,
    maxStreamParticles: Int = DefaultMaxStreamParticles,
): ButtonEmojiEffectState = remember(logTag, maxStreamParticles) {
    ButtonEmojiEffectState(
        logTag = logTag,
        maxStreamParticles = maxStreamParticles,
    )
}

@Composable
internal fun BoxScope.ButtonEmojiEffectLayer(
    effectState: ButtonEmojiEffectState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.matchParentSize()) {
        effectState.particles.forEach { particle ->
            key(particle.id) {
                ButtonEmojiParticleView(
                    particle = particle,
                    onFinished = effectState::removeParticle,
                )
            }
        }
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
internal fun TitleConfettiRain(visibleAlpha: Float) {
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
