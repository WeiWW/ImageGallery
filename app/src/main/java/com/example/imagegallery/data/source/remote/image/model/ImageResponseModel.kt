package com.example.imagegallery.data.source.remote.image.model

import com.google.gson.annotations.SerializedName

data class UploadSuccess(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("image")
    val image: Image
)

data class ImageList(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val images: List<Image>
)

data class Image(
    @SerializedName("url")
    val url: String,
    @SerializedName("size")
    val size: Int,
    @SerializedName("content_type")
    val contentType: String,
    @SerializedName("caption")
    val caption: String
)