package com.example.pixcels_assessment.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pixcels_assessment.R
import com.example.pixcels_assessment.ui.theme.Pixcels_assessmentTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    ciphertext: String,
    onBackClick: () -> Unit,
    onEncryptClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val copySuccessMessage = stringResource(R.string.copy_success_message)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(R.string.result_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back_description)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        clipboardManager.setText(AnnotatedString(ciphertext))
                        scope.launch {
                            snackbarHostState.showSnackbar(copySuccessMessage)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = stringResource(R.string.copy_description)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(R.string.result_header),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stringResource(R.string.result_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 4.dp
            ) {
                SelectionContainer {
                    Text(
                        text = ciphertext,
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            OutlinedButton(
                onClick = onEncryptClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(stringResource(R.string.encrypt_another_button))
            }
        }
    }
}

@Composable
fun SelectionContainer(content: @Composable () -> Unit) {
    // Basic wrapper to enable text selection if needed
    // In a real app we might use androidx.compose.foundation.text.selection.SelectionContainer
    content()
}

@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    Pixcels_assessmentTheme {
        ResultScreen(
            ciphertext = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqH2KwwF8sG91CCyu+FvWYPpNgFzmQlOt6NgIDkxLZ0RlGxWnVi0TPd3C1wmua1nYqm54sDrlBmZjMn3VIvboJFo4m2qqKXaLv+AwMy0/QxUuMTCvyExd/632W+uPGq2KgFM1a0+L+akCNNUJGlulOHUxeORg/G34kZSXPfxBa+M4YcPxiJSuI35FLSIfCplDi2qPN0cqHNvLcmdEKgb9DbXqwNhHhhSK5yWLBZlTKyLhIU0HewUJWG+J5GdUZv7dnUzE5Cl2SF4u91LKCLw3nSWoHrHwE+CGoNFNLC3ScBJbkWbLeVeFXNx5YTf2pUR4bVHTrglBwHtMuGq0NDnDiQIDAQAB", 
            onBackClick = {},
            onEncryptClick = {}
        )
    }
}
