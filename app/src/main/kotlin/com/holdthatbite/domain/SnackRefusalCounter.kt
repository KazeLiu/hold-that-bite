package com.holdthatbite.domain

object SnackRefusalCounter {
    fun increment(currentCount: Int): Int {
        return currentCount.coerceAtLeast(0) + 1
    }

    fun decrement(currentCount: Int): Int {
        return (currentCount - 1).coerceAtLeast(0)
    }
}
