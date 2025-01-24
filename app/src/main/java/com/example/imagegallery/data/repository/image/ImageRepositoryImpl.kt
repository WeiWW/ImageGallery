package com.example.imagegallery.data.repository.image

import com.example.imagegallery.data.common.Result
import com.example.imagegallery.data.repository.token.TokenRepository
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
                    } else if (response.code() == 401) {
                        Result.Unauthorized
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
                    } else if (response.code() == 401) {
                        Result.Unauthorized
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
        if (tokenResult !is Result.Success) return tokenResult as Result<T>

        val initialToken = tokenResult.data
        val initialResult = apiCall(initialToken)

        return if (initialResult is Result.Unauthorized) {
            when(val refreshTokenResult = onRefreshToken()){
                is Result.Success -> {
                    val newToken = refreshTokenResult.data
                    apiCall(newToken)
                }
                else -> return refreshTokenResult as Result<T>
            }

        } else {
            initialResult
        }
    }
}