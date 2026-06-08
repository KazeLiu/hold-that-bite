package com.holdthatbite.domain

data class SnackRefusalEncouragement(
    val shortLabel: String,
    val detail: String,
    val intensity: Int,
)

object Motivation {
    fun snackRefusalEncouragement(count: Int): SnackRefusalEncouragement? {
        val normalizedCount = count.coerceAtLeast(0)
        return when {
            normalizedCount >= 10 -> SnackRefusalEncouragement(
                shortLabel = "硬控住了",
                detail = "第 $normalizedCount 次，今天你是真的稳",
                intensity = 4,
            )
            normalizedCount >= 5 -> SnackRefusalEncouragement(
                shortLabel = "防线拉满",
                detail = "第 $normalizedCount 次，嘴馋也绕路走",
                intensity = 3,
            )
            normalizedCount >= 3 -> SnackRefusalEncouragement(
                shortLabel = "今天很稳",
                detail = "第 $normalizedCount 次，节奏守住了",
                intensity = 2,
            )
            normalizedCount >= 1 -> SnackRefusalEncouragement(
                shortLabel = "小胜利",
                detail = "第 $normalizedCount 次，先赢一口",
                intensity = 1,
            )
            else -> null
        }
    }
}
