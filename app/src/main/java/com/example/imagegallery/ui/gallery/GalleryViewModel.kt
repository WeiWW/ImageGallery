package com.example.imagegallery.ui.gallery

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.Coil
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.imagegallery.data.common.Result
import com.example.imagegallery.data.repository.image.ImageRepository
import com.example.imagegallery.data.source.remote.image.model.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchImages()
    }

    fun updateAuthState(hasAuthError: Boolean) {
        _uiState.update { it.copy(hasAuthError = hasAuthError) }
        fetchImages()
    }

    fun fetchImages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = imageRepository.getImages()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            images = result.data,
                            isLoading = false,
                            error = null,
                            hasAuthError = false
                        )
                    }
                }

                is Result.Unauthorized -> {
                    _uiState.update { it.copy(isLoading = false, hasAuthError = true) }
                }

                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.errorMsg) }
                }
            }
        }
    }

    fun updateImage(context: Context, uri: Uri) {
        _uiState.update { it.copy(isLoading = true, error = null, hasAuthError = false) }
        viewModelScope.launch {
            val byteArray = convertUriToByteArray(context, uri)

            if (byteArray == null) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to convert image")
                }
                return@launch
            }

            when (val result = imageRepository.uploadImage(byteArray)) {
                is Result.Success -> fetchImages()
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.errorMsg, hasAuthError = false)
                }

                Result.Unauthorized -> {
                    _uiState.update { it.copy(isLoading = false, hasAuthError = true) }
                }
            }
        }
    }

    internal suspend fun convertUriToByteArray(context: Context, uri: Uri): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val request = ImageRequest.Builder(context)
                    .data(uri)
                    .build()

                val result = Coil.imageLoader(context).execute(request)

                if (result is SuccessResult) {
                    val bitmap = result.drawable.toBitmap()
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.toByteArray()
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

data class GalleryUiState(
    val images: List<Image> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasAuthError: Boolean = false
)