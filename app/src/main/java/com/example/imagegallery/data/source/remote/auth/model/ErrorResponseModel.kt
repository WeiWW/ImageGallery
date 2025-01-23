package com.example.imagegallery.data.source.remote.auth.model

import com.google.gson.annotations.SerializedName

data class ErrorResponseModel(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("error")
    val error: String
)
