package com.example.imagegallery.data.repository.token
import com.example.imagegallery.data.common.Result

interface TokenRepository {
    suspend fun getToken(): Result<String>
    suspend fun saveToken(accessToken: String, refreshToken: String)
    suspend fun refreshToken(): Result<String>
    suspend fun login(username: String, password: String): Result<String>
}