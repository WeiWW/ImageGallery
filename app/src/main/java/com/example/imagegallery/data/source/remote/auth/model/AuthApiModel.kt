package com.example.imagegallery.data.source.remote.auth.model

import com.google.gson.annotations.SerializedName

data class LoginApiModel(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)

data class RefreshTokenApiModel(
    @SerializedName("refresh_token")
    val refreshToken: String
)

data class TokenResponseModel(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)