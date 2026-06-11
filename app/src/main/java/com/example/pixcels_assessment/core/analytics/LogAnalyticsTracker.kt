package com.example.pixcels_assessment.core.analytics

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogAnalyticsTracker @Inject constructor() : AnalyticsTracker {
    override fun trackEvent(eventName: String, params: Map<String, String>) {
        Log.i("Analytics", "Event: $eventName, Params: $params")
    }
}
