package com.arfakhsy.pixcels_assessment.core.analytics

interface AnalyticsTracker {
    fun trackEvent(eventName: String, params: Map<String, String> = emptyMap())
}

object AnalyticsEvents {
    const val ENCRYPTION_ATTEMPT = "ENCRYPTION_ATTEMPT"
    const val ENCRYPTION_RESULT = "ENCRYPTION_RESULT"
}
