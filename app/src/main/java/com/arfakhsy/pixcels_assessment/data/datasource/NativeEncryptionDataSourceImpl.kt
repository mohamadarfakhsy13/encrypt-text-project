package com.arfakhsy.pixcels_assessment.data.datasource

import androidx.annotation.Keep
import javax.inject.Inject
import javax.inject.Singleton

interface NativeEncryptionDataSource {
    fun encryptAndSend(input: String): String
    fun stringFromJNI(): String
}

@Singleton
class NativeEncryptionDataSourceImpl @Inject constructor() : NativeEncryptionDataSource {
    
    init {
        try {
            System.loadLibrary("pixcels_assessment")
        } catch (e: UnsatisfiedLinkError) {
            // Silently fail in environments where JNI is not available (e.g. JVM unit tests)
        }
    }

    override external fun encryptAndSend(input: String): String
    
    override external fun stringFromJNI(): String

    /**
     * JNI callback to download a URL using Kotlin/Java networking.
     * This fulfills the requirement of keeping logic in C++ while using safe platform I/O.
     * Annotated with @Keep to prevent renaming by ProGuard/R8.
     */
    @Keep
    fun downloadUrlKotlin(url: String): String {
        return try {
            java.net.URL(url).readText()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
