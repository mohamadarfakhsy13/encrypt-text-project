package com.example.pixcels_assessment.data.repository

import com.example.pixcels_assessment.data.datasource.NativeEncryptionDataSource
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EncryptionRepositoryImplTest {

    private val nativeDataSource = mockk<NativeEncryptionDataSource>()
    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository = EncryptionRepositoryImpl(nativeDataSource, testDispatcher)

    @Test
    fun `encrypt calls nativeDataSource encryptAndSend`() = runTest {
        val input = "test input"
        val expectedResult = "encrypted output"
        coEvery { nativeDataSource.encryptAndSend(input) } returns expectedResult

        val result = repository.encrypt(input)

        assertEquals(expectedResult, result)
        verify { nativeDataSource.encryptAndSend(input) }
    }
}
