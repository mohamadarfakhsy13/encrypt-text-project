package com.example.pixcels_assessment.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object InputRoute : NavKey

@Serializable
data class ResultRoute(val ciphertext: String) : NavKey
