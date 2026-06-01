package com.holdthatbite.ui

import com.holdthatbite.domain.WeightUnit

data class CheckInSupplement(
    val note: String?,
    val weightKg: Double?,
) {
    companion object {
        fun from(note: String, weight: String?, weightUnit: WeightUnit = WeightUnit.KG): CheckInSupplement {
            val cleanNote = note.trim().takeIf { it.isNotEmpty() }
            val parsedWeight = weight
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
                ?.toDoubleOrNull()
                ?.let(weightUnit::toKg)
            return CheckInSupplement(note = cleanNote, weightKg = parsedWeight)
        }
    }
}
