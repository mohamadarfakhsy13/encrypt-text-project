package com.arfakhsy.pixcels_assessment.domain.usecase

import com.arfakhsy.pixcels_assessment.data.repository.EncryptionRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class EncryptUseCaseTest {

    private val repository = mockk<EncryptionRepository>()
    private val useCase = EncryptUseCase(repository)

    @Test
    fun `invoke calls repository encrypt`() = runTest {
        val input = "test input"
        val expectedResult = "encrypted output"
        coEvery { repository.encrypt(input) } returns expectedResult

        val result = useCase(input)

        assertEquals(expectedResult, result)
        coVerify { repository.encrypt(input) }
    }
}
