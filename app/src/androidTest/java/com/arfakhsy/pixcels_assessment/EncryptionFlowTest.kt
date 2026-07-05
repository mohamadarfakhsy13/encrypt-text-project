package com.arfakhsy.pixcels_assessment

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EncryptionFlowTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testEncryptionFlow() {
        // 1. Verify InputScreen displays correctly
        val appTitle = composeTestRule.activity.getString(R.string.app_title)
        val inputLabel = composeTestRule.activity.getString(R.string.input_label)
        val encryptButtonText = composeTestRule.activity.getString(R.string.encrypt_button_text)

        composeTestRule.onNodeWithText(appTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(inputLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText(encryptButtonText).assertIsDisplayed()

        // 2. Perform encryption flow
        val testInput = "Hello Pi-Xcels Clean Architecture"
        composeTestRule.onNodeWithText(inputLabel).performTextInput(testInput)
        composeTestRule.onNodeWithText(encryptButtonText).performClick()

        // 3. Wait for ResultScreen
        val resultTitle = composeTestRule.activity.getString(R.string.result_title)
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText(resultTitle).fetchSemanticsNodes().isNotEmpty()
        }

        // 4. Verify ResultScreen displays ciphertext and buttons
        val resultHeader = composeTestRule.activity.getString(R.string.result_header)
        val copyDescription = composeTestRule.activity.getString(R.string.copy_description)
        val encryptAnotherText = composeTestRule.activity.getString(R.string.encrypt_another_button)

        composeTestRule.onNodeWithText(resultHeader).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(copyDescription).assertIsDisplayed()
        composeTestRule.onNodeWithText(encryptAnotherText).assertIsDisplayed()

        // 5. Verify clicking "Encrypt Another String" takes user back to InputScreen
        composeTestRule.onNodeWithText(encryptAnotherText).performClick()
        
        composeTestRule.onNodeWithText(inputLabel).assertIsDisplayed()
    }
}
