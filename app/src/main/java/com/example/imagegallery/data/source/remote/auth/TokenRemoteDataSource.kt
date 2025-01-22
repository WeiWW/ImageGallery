package com.example.imagegallery.data.source.remote.auth

import com.example.imagegallery.data.source.remote.auth.model.LoginApiModel
import com.example.imagegallery.data.source.remote.auth.model.RefreshTokenApiModel
import com.example.imagegallery.data.source.remote.auth.model.TokenResponseModel
import retrofit2.Response

interface TokenRemoteDataSource {
    suspend fun login(loginApiModel: LoginApiModel): Response<TokenResponseModel>

    suspend fun refreshToken(refreshToken: String): Response<TokenResponseModel>
}