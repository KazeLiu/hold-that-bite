package com.holdthatbite.ui

data class SnackRefusalFeedback(
    val count: Int,
    val id: Long,
) {
    val message: String
        get() = "拒绝零食 +1，今天第 ${count.coerceAtLeast(1)} 次"

    val undoLabel: String
        get() = "撤销"
}
