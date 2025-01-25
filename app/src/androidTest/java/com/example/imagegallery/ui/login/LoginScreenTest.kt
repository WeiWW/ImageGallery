package com.example.imagegallery.ui.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.imagegallery.data.common.TestTags
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun init_state_LoginScreen() {
        composeTestRule.setContent {
            LoginScreen(
                uiState = LoginUiState(),
                onLoginSuccess = {},
                onUserNameChange = {},
                onPasswordChange = {},
                onLoginClick = {}
            )
        }

        composeTestRule.onNodeWithTag(TestTags.TEXT_FIELD_USERNAME).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.TEXT_FIELD_USERNAME_ERROR).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.TEXT_FIELD_PASSWORD).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.TEXT_FIELD_PASSWORD_ERROR).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.BUTTON_LOGIN).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(TestTags.LOGIN_ERROR_MESSAGE).assertDoesNotExist()
    }

    @Test
    fun when_input_not_match_show_error() {
        composeTestRule.setContent {
            LoginScreen(
                uiState = LoginUiState(
                    username = "test",
                    password = "test",
                ),
                onLoginSuccess = {},
                onUserNameChange = {},
                onPasswordChange = {},
                onLoginClick = {}
            )
        }

        composeTestRule.onNodeWithTag(TestTags.TEXT_FIELD_USERNAME).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.TEXT_FIELD_USERNAME_ERROR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.TEXT_FIELD_PASSWORD).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.TEXT_FIELD_PASSWORD_ERROR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.BUTTON_LOGIN).assertIsNotEnabled()
        composeTestRule.onNodeWithTag(TestTags.LOGIN_ERROR_MESSAGE).assertDoesNotExist()
    }

    @Test
    fun when_input_match_regex_Login_Button_clickable() {

        composeTestRule.setContent {
            LoginScreen(
                uiState = LoginUiState(
                    isValidUsrName = true,
                    isValidPwd = true
                ),
                onLoginSuccess = {},
                onUserNameChange = {},
                onPasswordChange = {},
                onLoginClick = {}
            )
        }

        composeTestRule.onNodeWithTag(TestTags.TEXT_FIELD_USERNAME).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.TEXT_FIELD_USERNAME_ERROR).assertIsNotDisplayed()
        composeTestRule.onNodeWithTag(TestTags.TEXT_FIELD_PASSWORD).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.TEXT_FIELD_PASSWORD_ERROR).assertIsNotDisplayed()
        composeTestRule.onNodeWithTag(TestTags.BUTTON_LOGIN).assertIsEnabled()
        composeTestRule.onNodeWithTag(TestTags.LOGIN_ERROR_MESSAGE).assertDoesNotExist()
    }

    @Test
    fun when_has_errorMsg_show_error() {
        composeTestRule.setContent {
            LoginScreen(
                uiState = LoginUiState(
                    errorMessage = "Error Message"
                ),
                onLoginSuccess = {},
                onUserNameChange = {},
                onPasswordChange = {},
                onLoginClick = {}
            )
        }


        composeTestRule.onNodeWithTag(TestTags.LOGIN_ERROR_MESSAGE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.LOGIN_ERROR_MESSAGE)
            .assertTextEquals("Error Message")
    }
}