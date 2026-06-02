package com.holdthatbite.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeModeTest {
    @Test
    fun defaultSettingsFollowSystemTheme() {
        assertEquals(ThemeMode.SYSTEM, AppSettings().themeMode)
    }

    @Test
    fun themeModeLabelsAreUserFacingSettingsText() {
        assertEquals("跟随手机", ThemeMode.SYSTEM.label)
        assertEquals("浅色", ThemeMode.LIGHT.label)
        assertEquals("夜间", ThemeMode.DARK.label)
    }

    @Test
    fun themeModeResolvesDarkModeFromSystemOrOverride() {
        assertTrue(ThemeMode.SYSTEM.shouldUseDarkTheme(systemInDarkTheme = true))
        assertFalse(ThemeMode.SYSTEM.shouldUseDarkTheme(systemInDarkTheme = false))
        assertFalse(ThemeMode.LIGHT.shouldUseDarkTheme(systemInDarkTheme = true))
        assertTrue(ThemeMode.DARK.shouldUseDarkTheme(systemInDarkTheme = false))
    }
}
