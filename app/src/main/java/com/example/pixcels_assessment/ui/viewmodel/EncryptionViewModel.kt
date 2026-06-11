package com.example.pixcels_assessment.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixcels_assessment.core.analytics.AnalyticsEvents
import com.example.pixcels_assessment.core.analytics.AnalyticsTracker
import com.example.pixcels_assessment.domain.usecase.EncryptUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class EncryptionUiState {
    data object Idle : EncryptionUiState()
    data object Loading : EncryptionUiState()
    data class Success(val ciphertext: String) : EncryptionUiState()
    data class Error(val message: String) : EncryptionUiState()
}

@HiltViewModel
class EncryptionViewModel @Inject constructor(
    private val encryptUseCase: EncryptUseCase,
    private val analyticsTracker: AnalyticsTracker,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow<EncryptionUiState>(EncryptionUiState.Idle)
    val uiState: StateFlow<EncryptionUiState> = _uiState.asStateFlow()

    fun encrypt(input: String) {
        viewModelScope.launch {
            analyticsTracker.trackEvent(AnalyticsEvents.ENCRYPTION_ATTEMPT, mapOf("input_length" to input.length.toString()))
            _uiState.value = EncryptionUiState.Loading
            try {
                // Use withContext to ensure the dispatcher is used
                val result = withContext(ioDispatcher) {
                    encryptUseCase(input)
                }
                if (result.startsWith("Error")) {
                    _uiState.value = EncryptionUiState.Error(result)
                    analyticsTracker.trackEvent(AnalyticsEvents.ENCRYPTION_RESULT, mapOf("status" to "error", "message" to result))
                } else {
                    _uiState.value = EncryptionUiState.Success(result)
                    analyticsTracker.trackEvent(AnalyticsEvents.ENCRYPTION_RESULT, mapOf("status" to "success"))
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error occurred"
                _uiState.value = EncryptionUiState.Error(errorMessage)
                analyticsTracker.trackEvent(AnalyticsEvents.ENCRYPTION_RESULT, mapOf("status" to "exception", "message" to errorMessage))
            }
        }
    }

    fun resetState() {
        _uiState.value = EncryptionUiState.Idle
    }
}
