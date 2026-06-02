package com.holdthatbite.analytics

import android.content.Context
import android.util.Log
import com.holdthatbite.BuildConfig
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

class AnalyticsTracker(private val context: Context) {
    private val appContext = context.applicationContext
    private var initialized = false
    private var enabled = false

    init {
        UMConfigure.preInit(appContext, AppKey, DefaultChannel)
        debugLog("preInit")
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
        if (enabled) {
            ensureInitialized()
        } else if (initialized) {
            UMConfigure.submitPolicyGrantResult(appContext, false)
            debugLog("disabled")
        }
    }

    fun track(event: AnalyticsEvent) {
        if (!enabled) return
        ensureInitialized()
        MobclickAgent.onEvent(appContext, event.id)
        debugLog("event=${event.id}")
    }

    private fun ensureInitialized() {
        if (initialized) return
        UMConfigure.setLogEnabled(BuildConfig.DEBUG)
        UMConfigure.submitPolicyGrantResult(appContext, true)
        UMConfigure.init(appContext, AppKey, DefaultChannel, UMConfigure.DEVICE_TYPE_PHONE, null)
        initialized = true
        debugLog("init")
    }

    private fun debugLog(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(LogTag, message)
        }
    }

    private companion object {
        const val LogTag = "HoldThatBiteAnalytics"
        const val AppKey = "6a1e645b9a7f376488eda983"
        const val DefaultChannel = "official"
    }
}

enum class AnalyticsEvent(val id: String) {
    APP_OPEN("app_open"),
    BITE_KEPT("bite_kept"),
    BITE_MISSED("bite_missed"),
    SNACK_REFUSAL_ADDED("snack_refusal_added"),
    WEIGHT_TREND_ENABLED("weight_trend_enabled"),
    WEIGHT_TREND_DISABLED("weight_trend_disabled"),
    WEIGHT_TREND_OPENED("weight_trend_opened"),
    WEIGHT_RECORD_CREATED("weight_record_created"),
    SETTINGS_OPENED("settings_opened"),
    PRIVACY_POLICY_OPENED("privacy_policy_opened"),
}
