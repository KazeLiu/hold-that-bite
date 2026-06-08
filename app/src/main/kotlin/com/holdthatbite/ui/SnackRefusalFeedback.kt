package com.holdthatbite.ui

import com.holdthatbite.domain.Motivation

data class SnackRefusalFeedback(
    val count: Int,
    val id: Long,
) {
    val message: String
        get() = Motivation.snackRefusalEncouragement(count)
            ?.let { "拒绝零食 +1，${it.shortLabel} · ${it.detail}" }
            ?: "拒绝零食 +1，今天先赢一口"

    val undoLabel: String
        get() = "撤销"
}
