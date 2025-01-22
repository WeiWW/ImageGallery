package com.example.imagegallery.data.source.remote.auth

import com.example.imagegallery.data.source.remote.auth.model.LoginApiModel
import com.example.imagegallery.data.source.remote.auth.model.RefreshTokenApiModel
import com.example.imagegallery.data.source.remote.auth.model.TokenResponseModel
import retrofit2.Response
import javax.inject.Inject

class TokenRemoteDataSourceImpl @Inject constructor(
    private val authApi: AuthApi
) : TokenRemoteDataSource {
    override suspend fun login(loginApiModel: LoginApiModel): Response<TokenResponseModel> {
        return authApi.login(loginApiModel)
    }

    override suspend fun refreshToken(refreshToken: String): Response<TokenResponseModel> {
        return authApi.refreshToken(refreshToken)
    }
}