package com.example.imagegallery.data.repository.image

import com.example.imagegallery.data.common.Result
import com.example.imagegallery.data.common.getErrorMsg
import com.example.imagegallery.data.repository.TokenRepository
import com.example.imagegallery.data.source.remote.image.ImageDataSource
import com.example.imagegallery.data.source.remote.image.model.Image
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val imageDataSource: ImageDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ImageRepository {

    override suspend fun getImages(): Result<List<Image>> {
        return withContext(ioDispatcher) {
            executeWithTokenRetry(
                apiCall = { token ->
                    val response = imageDataSource.getImages(token)
                    if (response.isSuccessful) {
                        Result.Success(response.body()?.images ?: emptyList())
                    } else {
                        Result.Error("Failed to get images: ${response.code()}")
                    }
                },
                onRefreshToken = { tokenRepository.refreshToken() }
            )
        }
    }


    override suspend fun uploadImage(image: ByteArray): Result<Unit> {
        return withContext(ioDispatcher) {
            executeWithTokenRetry(
                apiCall = { token ->
                    val response = imageDataSource.uploadImage(token, image)
                    if (response.isSuccessful) {
                        Result.Success(Unit)
                    } else {
                        Result.Error("Failed to upload image: ${response.code()}")
                    }
                },
                onRefreshToken = { tokenRepository.refreshToken() }
            )
        }
    }

    private suspend fun <T> executeWithTokenRetry(
        apiCall: suspend (String) -> Result<T>,
        onRefreshToken: suspend () -> Result<String>
    ): Result<T> {
        val tokenResult = tokenRepository.getToken()
        if (tokenResult is Result.Error) return tokenResult

        val initialToken = (tokenResult as Result.Success).data
        val initialResult = apiCall(initialToken)

        return if (initialResult is Result.Error && initialResult.getErrorMsg()
                .contains("401")
        ) {
            val refreshTokenResult = onRefreshToken()
            if (refreshTokenResult is Result.Error) return refreshTokenResult

            val newToken = (refreshTokenResult as Result.Success).data
            apiCall(newToken)
        } else {
            initialResult
        }
    }
}