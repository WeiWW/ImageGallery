package com.example.imagegallery.data.source.remote.image

import com.example.imagegallery.data.source.remote.image.model.ImageList
import com.example.imagegallery.data.source.remote.image.model.UploadSuccess
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject

class ImageDataSourceImpl @Inject constructor(private val imageApi: ImageApi) : ImageDataSource {

    override suspend fun getImages(accessToken: String): Response<ImageList> {
        return imageApi.getImages(token = "Bearer $accessToken")
    }

    override suspend fun uploadImage(
        accessToken: String,
        image: ByteArray
    ): Response<UploadSuccess> {
        return imageApi.postImage(token = "Bearer $accessToken", file = image.toMultipartBody())
    }

    private fun ByteArray.toMultipartBody(
        partName: String = "file",
        fileName: String = "image.jpg"
    ): MultipartBody.Part {
        val requestBody = this.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, this.size)
        return MultipartBody.Part.createFormData(partName, fileName, requestBody)
    }
}