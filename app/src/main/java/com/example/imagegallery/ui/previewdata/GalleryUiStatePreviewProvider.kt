package com.example.imagegallery.ui.previewdata

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.imagegallery.data.source.remote.image.model.Image
import com.example.imagegallery.ui.gallery.GalleryUiState

class GalleryUiStatePreviewProvider : PreviewParameterProvider<GalleryUiState> {
    override val values = sequenceOf(
        GalleryUiState(
            isLoading = true
        ),
        GalleryUiState(
            images = listOf(
                Image(
                    "",
                    5000,
                    "image/*",
                    ""
                ),
                Image(
                    "",
                    5000,
                    "image/*",
                    ""
                ),
                Image(
                    "",
                    5000,
                    "image/*",
                    ""
                )
            ),

        )
    )
}
