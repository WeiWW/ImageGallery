package com.example.imagegallery.data.source.local

interface TokenLocalDataSource {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun saveTokens(accessToken: String, refreshToken: String)
}