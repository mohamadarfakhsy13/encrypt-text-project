package com.arfakhsy.pixcels_assessment.data.repository

import com.arfakhsy.pixcels_assessment.data.datasource.NativeEncryptionDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionRepositoryImpl @Inject constructor(
    private val nativeDataSource: NativeEncryptionDataSource,
    private val ioDispatcher: CoroutineDispatcher
) : EncryptionRepository {
    override suspend fun encrypt(input: String): String = withContext(ioDispatcher) {
        nativeDataSource.encryptAndSend(input)
    }
}
