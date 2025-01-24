package com.example.imagegallery.data.repository.image

import com.example.imagegallery.data.common.Result
import com.example.imagegallery.data.source.remote.image.model.Image

interface ImageRepository {
    suspend fun getImages(): Result<List<Image>>
    suspend fun uploadImage(image: ByteArray): Result<Unit>
}