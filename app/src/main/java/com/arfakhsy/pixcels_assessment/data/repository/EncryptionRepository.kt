package com.arfakhsy.pixcels_assessment.data.repository

interface EncryptionRepository {
    suspend fun encrypt(input: String): String
}
