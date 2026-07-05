package com.arfakhsy.pixcels_assessment.viewmodel

import app.cash.turbine.test
import com.arfakhsy.pixcels_assessment.core.analytics.AnalyticsEvents
import com.arfakhsy.pixcels_assessment.core.analytics.AnalyticsTracker
import com.arfakhsy.pixcels_assessment.domain.usecase.EncryptUseCase
import com.arfakhsy.pixcels_assessment.ui.viewmodel.EncryptionUiState
import com.arfakhsy.pixcels_assessment.ui.viewmodel.EncryptionViewModel
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EncryptionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val encryptUseCase = mockk<EncryptUseCase>()
    private val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `encrypt success transitions correctly and logs analytics`() = runTest {
        coEvery { encryptUseCase("test") } returns "EncryptedData"
        val viewModel = EncryptionViewModel(encryptUseCase, analyticsTracker, testDispatcher)

        viewModel.uiState.test {
            assertEquals(EncryptionUiState.Idle, awaitItem())
            
            viewModel.encrypt("test")
            
            assertEquals(EncryptionUiState.Loading, awaitItem())
            assertEquals(EncryptionUiState.Success("EncryptedData"), awaitItem())

            verify { 
                analyticsTracker.trackEvent(AnalyticsEvents.ENCRYPTION_ATTEMPT, any())
                analyticsTracker.trackEvent(AnalyticsEvents.ENCRYPTION_RESULT, match { it["status"] == "success" })
            }
        }
    }

    @Test
    fun `encrypt failure with Error string transitions to Error state and logs failure`() = runTest {
        coEvery { encryptUseCase("test") } returns "Error: Failed"
        val viewModel = EncryptionViewModel(encryptUseCase, analyticsTracker, testDispatcher)

        viewModel.uiState.test {
            assertEquals(EncryptionUiState.Idle, awaitItem())
            
            viewModel.encrypt("test")
            
            assertEquals(EncryptionUiState.Loading, awaitItem())
            assertEquals(EncryptionUiState.Error("Error: Failed"), awaitItem())

            verify { 
                analyticsTracker.trackEvent(AnalyticsEvents.ENCRYPTION_RESULT, match { it["status"] == "error" })
            }
        }
    }

    @Test
    fun `encrypt exception transitions to Error state and logs exception`() = runTest {
        coEvery { encryptUseCase("test") } throws Exception("Crash")
        val viewModel = EncryptionViewModel(encryptUseCase, analyticsTracker, testDispatcher)

        viewModel.uiState.test {
            assertEquals(EncryptionUiState.Idle, awaitItem())
            
            viewModel.encrypt("test")
            
            assertEquals(EncryptionUiState.Loading, awaitItem())
            assertEquals(EncryptionUiState.Error("Crash"), awaitItem())

            verify { 
                analyticsTracker.trackEvent(AnalyticsEvents.ENCRYPTION_RESULT, match { it["status"] == "exception" })
            }
        }
    }

    @Test
    fun `resetState returns state to Idle`() = runTest {
        coEvery { encryptUseCase("test") } returns "EncryptedData"
        val viewModel = EncryptionViewModel(encryptUseCase, analyticsTracker, testDispatcher)

        viewModel.uiState.test {
            assertEquals(EncryptionUiState.Idle, awaitItem())
            
            viewModel.encrypt("test")
            assertEquals(EncryptionUiState.Loading, awaitItem())
            assertEquals(EncryptionUiState.Success("EncryptedData"), awaitItem())
            
            viewModel.resetState()
            assertEquals(EncryptionUiState.Idle, awaitItem())
        }
    }
}
