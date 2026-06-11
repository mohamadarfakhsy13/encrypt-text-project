package com.example.pixcels_assessment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.pixcels_assessment.navigation.InputRoute
import com.example.pixcels_assessment.navigation.ResultRoute
import com.example.pixcels_assessment.ui.screens.InputScreen
import com.example.pixcels_assessment.ui.screens.ResultScreen
import com.example.pixcels_assessment.ui.theme.Pixcels_assessmentTheme
import com.example.pixcels_assessment.ui.viewmodel.EncryptionUiState
import com.example.pixcels_assessment.ui.viewmodel.EncryptionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pixcels_assessmentTheme {
                val backStack = rememberNavBackStack(elements = arrayOf(InputRoute))
                val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
                
                val viewModel: EncryptionViewModel = hiltViewModel()
                
                val uiState by viewModel.uiState.collectAsState()

                val onNavigateBackAndReset = {
                    if (backStack.size > 1) {
                        backStack.removeAt(backStack.size - 1)
                        viewModel.resetState()
                    } else {
                        finish()
                    }
                }

                LaunchedEffect(uiState) {
                    (uiState as? EncryptionUiState.Success)?.ciphertext?.let { ciphertext ->
                        if (backStack.none { it is ResultRoute && it.ciphertext == ciphertext }) {
                            backStack.removeAll { it is ResultRoute }
                            backStack.add(ResultRoute(ciphertext = ciphertext))
                        }
                    }
                }
                
                NavDisplay(
                    backStack = backStack,
                    onBack = onNavigateBackAndReset,
                    sceneStrategy = listDetailStrategy,
                    entryProvider = { route ->
                        when (route) {
                            is InputRoute -> {
                                NavEntry(
                                    key = InputRoute,
                                    metadata = ListDetailSceneStrategy.listPane()
                                ) {
                                    InputScreen(
                                        isLoading = uiState is EncryptionUiState.Loading,
                                        errorMessage = (uiState as? EncryptionUiState.Error)?.message,
                                        onEncryptClick = { input -> viewModel.encrypt(input) }
                                    )
                                }
                            }
                            is ResultRoute -> {
                                NavEntry(
                                    key = route,
                                    metadata = ListDetailSceneStrategy.detailPane()
                                ) {
                                    ResultScreen(
                                        ciphertext = route.ciphertext,
                                        onBackClick = onNavigateBackAndReset,
                                        onEncryptClick = onNavigateBackAndReset
                                    )
                                }
                            }
                            else -> NavEntry(route) { }
                        }
                    }
                )
            }
        }
    }
}
