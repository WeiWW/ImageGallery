package com.example.imagegallery.data.source.local

import javax.inject.Inject

class TokenLocalDataSourceImpl @Inject constructor(private val secureTokenManager: SecureTokenManager) :
    TokenLocalDataSource {
    override fun getAccessToken(): String? = secureTokenManager.getToken(ACCESS_TOKEN_KEY)

    override fun getRefreshToken(): String? = secureTokenManager.getToken(REFRESH_TOKEN_KEY)

    override fun saveTokens(accessToken: String, refreshToken: String) {
        secureTokenManager.saveToken(ACCESS_TOKEN_KEY, accessToken)
        secureTokenManager.saveToken(REFRESH_TOKEN_KEY, refreshToken)
    }

    companion object{
        internal const val ACCESS_TOKEN_KEY = "access_token"
        internal const val REFRESH_TOKEN_KEY = "refresh_token"
    }
}