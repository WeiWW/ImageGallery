package com.example.imagegallery.data.source.remote.auth

import com.example.imagegallery.data.source.remote.auth.model.LoginApiModel
import com.example.imagegallery.data.source.remote.auth.model.RefreshTokenApiModel
import com.example.imagegallery.data.source.remote.auth.model.TokenResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/login")
    suspend fun login(@Body loginApiModel: LoginApiModel): Response<TokenResponseModel>
    @POST("/auth/refresh")
    suspend fun refreshToken(@Body refreshToken: String): Response<TokenResponseModel>
}