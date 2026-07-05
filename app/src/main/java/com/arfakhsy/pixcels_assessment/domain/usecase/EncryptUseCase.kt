package com.arfakhsy.pixcels_assessment.domain.usecase

import com.arfakhsy.pixcels_assessment.data.repository.EncryptionRepository
import javax.inject.Inject

class EncryptUseCase @Inject constructor(
    private val repository: EncryptionRepository
) {
    suspend operator fun invoke(input: String): String {
        return repository.encrypt(input)
    }
}
