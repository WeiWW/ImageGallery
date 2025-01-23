package com.example.imagegallery.data.source.remote.image

import com.example.imagegallery.data.source.remote.image.model.ImageList
import com.example.imagegallery.data.source.remote.image.model.UploadSuccess
import retrofit2.Response

interface ImageDataSource {
    suspend fun uploadImage(accessToken: String, image: ByteArray): Response<UploadSuccess>
    suspend fun getImages(accessToken: String): Response<ImageList>
}