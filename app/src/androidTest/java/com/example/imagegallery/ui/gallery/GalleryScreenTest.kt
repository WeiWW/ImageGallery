package com.example.imagegallery.ui.gallery

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.imagegallery.data.common.TestTags
import com.example.imagegallery.data.source.remote.image.model.Image
import org.junit.Rule
import org.junit.Test

class GalleryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun init_state_GalleryScreen() {
        composeTestRule.setContent {
            GalleryScreen(
                uiState = GalleryUiState(
                    images = listOf(
                        Image(
                        "",
                        5000,
                        "image/*",
                        ""
                    )
                    ),
                    isLoading = false,
                    error = null,
                    hasAuthError = false
                ),
                onRefresh = {},
                onUpdateAuthState = {},
                onUpLoadImage = { _, _ -> })
        }

        composeTestRule.onNodeWithTag(TestTags.FAB_ADD_IMAGE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.DIALOG_LOGIN).assertIsNotDisplayed()
        composeTestRule.onNodeWithTag(TestTags.IMAGE_GRID).assertIsDisplayed()
    }

    @Test
    fun when_is_not_null_show_it() {
        val errorMsg = "error"
        composeTestRule.setContent {
            GalleryScreen(
                uiState = GalleryUiState(
                    images = emptyList(),
                    isLoading = false,
                    error = errorMsg,
                    hasAuthError = false
                ),
                onRefresh = {},
                onUpdateAuthState = {},
                onUpLoadImage = { _, _ -> })
        }

        composeTestRule.onNodeWithTag(TestTags.FAB_ADD_IMAGE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.DIALOG_LOGIN).assertIsNotDisplayed()
        composeTestRule.onNodeWithTag(TestTags.IMAGE_GRID).assertIsDisplayed()
        composeTestRule.onNodeWithText(errorMsg).assertIsDisplayed()
    }
}