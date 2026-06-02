package com.holdthatbite

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.util.Log
import com.holdthatbite.domain.AppSettings

internal enum class LauncherShortcutAction {
    SNACK_REFUSAL,
    KEPT_CHECK_IN,
    RECORD_WEIGHT,
}

internal data class LauncherShortcutSpec(
    val action: LauncherShortcutAction,
    val shortLabel: String,
    val longLabel: String,
    val rank: Int,
)

internal object LauncherShortcuts {
    const val ACTION_SNACK_REFUSAL = "com.holdthatbite.action.SNACK_REFUSAL"
    const val ACTION_KEPT_CHECK_IN = "com.holdthatbite.action.KEPT_CHECK_IN"
    const val ACTION_RECORD_WEIGHT = "com.holdthatbite.action.RECORD_WEIGHT"

    fun actionFrom(action: String?): LauncherShortcutAction? {
        return when (action) {
            ACTION_SNACK_REFUSAL -> LauncherShortcutAction.SNACK_REFUSAL
            ACTION_KEPT_CHECK_IN -> LauncherShortcutAction.KEPT_CHECK_IN
            ACTION_RECORD_WEIGHT -> LauncherShortcutAction.RECORD_WEIGHT
            else -> null
        }
    }

    fun dynamicActions(settings: AppSettings): List<LauncherShortcutAction> {
        return dynamicSpecs(settings).map { it.action }
    }

    fun dynamicSpecs(settings: AppSettings): List<LauncherShortcutSpec> {
        return if (settings.weightTrendEnabled) {
            listOf(
                LauncherShortcutSpec(
                    action = LauncherShortcutAction.RECORD_WEIGHT,
                    shortLabel = "记体重",
                    longLabel = "记录体重",
                    rank = 0,
                )
            )
        } else {
            emptyList()
        }
    }

    fun publishDynamic(context: Context, settings: AppSettings) {
        val manager = context.getSystemService(ShortcutManager::class.java) ?: return
        val shortcuts = dynamicSpecs(settings).map { spec ->
            spec.toShortcutInfo(context)
        }
        runCatching {
            manager.setDynamicShortcuts(shortcuts)
        }.onFailure { error ->
            Log.w(LogTag, "Failed to publish launcher shortcuts", error)
        }
    }

    private fun LauncherShortcutSpec.toShortcutInfo(context: Context): ShortcutInfo {
        return when (action) {
            LauncherShortcutAction.RECORD_WEIGHT -> ShortcutInfo.Builder(context, "record_weight")
                .setShortLabel(shortLabel)
                .setLongLabel(longLabel)
                .setIcon(Icon.createWithResource(context, R.drawable.ic_shortcut_weight))
                .setIntent(shortcutIntent(context, ACTION_RECORD_WEIGHT))
                .setRank(rank)
                .build()
            LauncherShortcutAction.SNACK_REFUSAL,
            LauncherShortcutAction.KEPT_CHECK_IN -> error("Only weight is published dynamically")
        }
    }

    private fun shortcutIntent(context: Context, action: String): Intent {
        return Intent(context, MainActivity::class.java)
            .setAction(action)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }

    private const val LogTag = "HoldThatBiteShortcuts"
}
