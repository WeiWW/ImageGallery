package com.example.imagegallery.data.source.remote.image

import com.example.imagegallery.data.source.remote.image.model.ImageList
import com.example.imagegallery.data.source.remote.image.model.UploadSuccess
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ImageApi {

    @Multipart
    @POST("/images")
    suspend fun postImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("caption") description: String = ""
    ):Response<UploadSuccess>

    @GET("/images")
    suspend fun getImages(@Header("Authorization") token: String):Response<ImageList>

    @Headers("Accept: image/jpeg")
    @GET("/images/{path}")
    suspend fun getImage(@Path("path") path: String)
}