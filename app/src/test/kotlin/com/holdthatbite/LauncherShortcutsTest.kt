package com.holdthatbite

import com.holdthatbite.domain.AppSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LauncherShortcutsTest {
    @Test
    fun knownActionsResolveToShortcutActions() {
        assertEquals(
            LauncherShortcutAction.SNACK_REFUSAL,
            LauncherShortcuts.actionFrom(LauncherShortcuts.ACTION_SNACK_REFUSAL),
        )
        assertEquals(
            LauncherShortcutAction.KEPT_CHECK_IN,
            LauncherShortcuts.actionFrom(LauncherShortcuts.ACTION_KEPT_CHECK_IN),
        )
        assertEquals(
            LauncherShortcutAction.RECORD_WEIGHT,
            LauncherShortcuts.actionFrom(LauncherShortcuts.ACTION_RECORD_WEIGHT),
        )
    }

    @Test
    fun unknownActionsAreIgnored() {
        assertNull(LauncherShortcuts.actionFrom(null))
        assertNull(LauncherShortcuts.actionFrom("com.holdthatbite.action.UNKNOWN"))
    }

    @Test
    fun weightShortcutIsDynamicOnlyWhenTrendIsEnabled() {
        assertEquals(
            emptyList<LauncherShortcutAction>(),
            LauncherShortcuts.dynamicActions(AppSettings(weightTrendEnabled = false)),
        )
        assertEquals(
            listOf(LauncherShortcutAction.RECORD_WEIGHT),
            LauncherShortcuts.dynamicActions(AppSettings(weightTrendEnabled = true)),
        )
    }

    @Test
    fun dynamicWeightShortcutUsesPlainLabelAndHighestRank() {
        val shortcut = LauncherShortcuts.dynamicSpecs(AppSettings(weightTrendEnabled = true)).single()

        assertEquals(LauncherShortcutAction.RECORD_WEIGHT, shortcut.action)
        assertEquals("记体重", shortcut.shortLabel)
        assertEquals("记录体重", shortcut.longLabel)
        assertEquals(0, shortcut.rank)
    }
}
