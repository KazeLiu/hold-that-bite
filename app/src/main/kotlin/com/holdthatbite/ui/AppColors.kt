package com.holdthatbite.ui

import androidx.compose.ui.graphics.Color

data class AppColorPalette(
    val primary: Color,
    val background: Color,
    val surface: Color,
    val surfaceSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val neutral: Color,
    val success: Color,
    val successSoft: Color,
    val missed: Color,
    val missedSoft: Color,
    val fastingEase: Color,
    val fastingEaseSoft: Color,
    val fastingModerate: Color,
    val fastingModerateSoft: Color,
    val fastingHard: Color,
    val fastingHardSoft: Color,
)

object AppColors {
    val ThemeBlue = Color(0xFF23ADE5)
    val Background = Color(0xFFF3FAFD)
    val Surface = Color.White
    val SurfaceSubtle = Color(0xFFFAFDFF)
    val TextPrimary = Color(0xFF20313A)
    val TextSecondary = Color(0xFF74848C)
    val Border = Color(0xFFD9EBF2)
    val Neutral = Color(0xFFEAF4F8)

    val DarkBackground = Color(0xFF10181D)
    val DarkSurface = Color(0xFF172329)
    val DarkSurfaceSubtle = Color(0xFF1E2D34)
    val DarkTextPrimary = Color(0xFFEAF7FB)
    val DarkTextSecondary = Color(0xFFA7BDC6)
    val DarkBorder = Color(0xFF31464F)
    val DarkNeutral = Color(0xFF22323A)
    val DarkWeightIncreaseSoft = Color(0xFF4A343A)
    val DarkWeightDecreaseSoft = Color(0xFF2F4C3E)
    val DarkFastingEaseSoft = Color(0xFF234638)
    val DarkFastingModerateSoft = Color(0xFF4A4025)
    val DarkFastingHardSoft = Color(0xFF4A3034)

    val StatusKept = Color(0xFF66B982)
    val StatusMissed = Color(0xFFE57A82)
    val FastingEase = Color(0xFF42A66A)
    val FastingModerate = Color(0xFFD39B23)
    val FastingHard = Color(0xFFE36F78)

    val WeightIncreaseSoft = Color(0xFFFBE4E6)
    val WeightDecreaseSoft = Color(0xFFDDF4E5)
    val FastingEaseSoft = Color(0xFFE1F5E8)
    val FastingModerateSoft = Color(0xFFFFF2CD)
    val FastingHardSoft = Color(0xFFFBE1E4)

    val CelebrationBlueDeep = Color(0xFF1489BC)
    val CelebrationGold = Color(0xFFFFC83D)
    val CelebrationPink = Color(0xFFFF8AB3)
    val CelebrationMint = Color(0xFF8FE0B2)
    val CelebrationSky = Color(0xFFA7DFFF)

    val LightPalette = AppColorPalette(
        primary = ThemeBlue,
        background = Background,
        surface = Surface,
        surfaceSubtle = SurfaceSubtle,
        textPrimary = TextPrimary,
        textSecondary = TextSecondary,
        border = Border,
        neutral = Neutral,
        success = StatusKept,
        successSoft = WeightDecreaseSoft,
        missed = StatusMissed,
        missedSoft = WeightIncreaseSoft,
        fastingEase = FastingEase,
        fastingEaseSoft = FastingEaseSoft,
        fastingModerate = FastingModerate,
        fastingModerateSoft = FastingModerateSoft,
        fastingHard = FastingHard,
        fastingHardSoft = FastingHardSoft,
    )

    val DarkPalette = AppColorPalette(
        primary = ThemeBlue,
        background = DarkBackground,
        surface = DarkSurface,
        surfaceSubtle = DarkSurfaceSubtle,
        textPrimary = DarkTextPrimary,
        textSecondary = DarkTextSecondary,
        border = DarkBorder,
        neutral = DarkNeutral,
        success = StatusKept,
        successSoft = DarkWeightDecreaseSoft,
        missed = StatusMissed,
        missedSoft = DarkWeightIncreaseSoft,
        fastingEase = Color(0xFF7BD79B),
        fastingEaseSoft = DarkFastingEaseSoft,
        fastingModerate = Color(0xFFFFD26A),
        fastingModerateSoft = DarkFastingModerateSoft,
        fastingHard = Color(0xFFFF9AA2),
        fastingHardSoft = DarkFastingHardSoft,
    )
}
