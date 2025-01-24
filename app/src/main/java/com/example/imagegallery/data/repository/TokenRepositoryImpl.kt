package com.example.imagegallery.data.repository

import com.example.imagegallery.data.common.Result
import com.example.imagegallery.data.source.local.TokenLocalDataSource
import com.example.imagegallery.data.source.remote.auth.TokenRemoteDataSource
import com.example.imagegallery.data.source.remote.auth.model.LoginApiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val tokenRemoteDataSource: TokenRemoteDataSource,
    private val tokenLocalDataSource: TokenLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TokenRepository {

    override suspend fun login(username: String, password: String): Result<String> {
        // Call the login API using the remote data source
        return withContext(ioDispatcher) {
            try {
                val response = tokenRemoteDataSource.login(LoginApiModel(username, password))
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    // Save the access token and refresh token to the local data source
                    tokenLocalDataSource.saveTokens(
                        accessToken = body.accessToken,
                        refreshToken = body.refreshToken
                    )
                    Result.Success(body.accessToken)
                } else {
                    Result.Error(response.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                Result.Error(e.message ?: "An error occurred while logging in")
            }
        }
    }

    override suspend fun getToken(): Result<String> = withContext(ioDispatcher) {
        // Get the access token from the local data source
        val accessToken = tokenLocalDataSource.getAccessToken()
            ?: return@withContext Result.Error("Access token is null")
        return@withContext Result.Success(accessToken)
    }

    override suspend fun saveToken(accessToken: String, refreshToken: String) {
        withContext(ioDispatcher){ tokenLocalDataSource.saveTokens(accessToken = accessToken, refreshToken = refreshToken) }
    }

    override suspend fun refreshToken(): Result<String>  = withContext(ioDispatcher) {
        // Get the refresh token from the local data source
        val refreshToken = tokenLocalDataSource.getRefreshToken()
            ?: return@withContext Result.Error("Refresh token is null")

        // Get the new access token from the remote data source using the refresh token
        return@withContext try {
            val response = tokenRemoteDataSource.refreshToken(refreshToken)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                // Save the new access token to the local data source
                tokenLocalDataSource.saveTokens(
                    accessToken = body.accessToken,
                    refreshToken = body.refreshToken
                )
                Result.Success(body.accessToken)
            } else if(response.code() == 401){
                Result.Unauthorized
            }else {
                Result.Error(response.errorBody()?.string() ?: "Unknown error")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred while refreshing the token")
        }
    }
}