package com.holdthatbite.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class SnackRefusalFeedbackTest {
    @Test
    fun messageShowsCurrentSnackRefusalCount() {
        val feedback = SnackRefusalFeedback(count = 3, id = 1L)

        assertEquals("拒绝零食 +1，今天第 3 次", feedback.message)
        assertEquals("撤销", feedback.undoLabel)
    }

    @Test
    fun messageDoesNotShowCountBelowOne() {
        val feedback = SnackRefusalFeedback(count = 0, id = 1L)

        assertEquals("拒绝零食 +1，今天第 1 次", feedback.message)
    }
}
