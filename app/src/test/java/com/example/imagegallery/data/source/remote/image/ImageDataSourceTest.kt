package com.example.imagegallery.data.source.remote.image

import com.example.imagegallery.data.source.remote.image.model.ImageList
import com.example.imagegallery.data.source.remote.image.model.UploadSuccess
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class ImageDataSourceTest {

    private lateinit var imageDataSource: ImageDataSourceImpl

    @MockK
    private lateinit var imageApi: ImageApi

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        imageDataSource = ImageDataSourceImpl(imageApi)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getImages return ImageList when imageApi response successfully`() = runBlocking {
        // Arrange
        coEvery { imageApi.getImages(any()) } returns Response.success(ImageList(true, emptyList()))

        // Act
        val response = imageDataSource.getImages(ACCESS_TOKEN)

        // Assert
        response.body().apply {
            this shouldNotBeEqualTo null
            this?.success shouldBeEqualTo true
            this?.images shouldBeInstanceOf List::class
        }
        coVerify(exactly = 1) { imageApi.getImages(any()) }
    }

    @Test
    fun `getImages return error when imageApi response failed`() = runBlocking {
        // Arrange
        coEvery { imageApi.getImages(any()) } returns Response.error(401, "error".toResponseBody())

        // Act
        val response = imageDataSource.getImages(ACCESS_TOKEN)

        // Assert
        response.isSuccessful shouldBeEqualTo false
        response.errorBody() shouldNotBeEqualTo null
        coVerify(exactly = 1) { imageApi.getImages(any()) }
    }

    @Test
    fun `uploadImage return UploadSuccess when imageApi response successfully`(): Unit = runBlocking {
        // Arrange
        val image = byteArrayOf(1, 2, 3)
        val slot = slot<MultipartBody.Part>()
        coEvery { imageApi.postImage(any(), capture(slot)) } returns Response.success(mockk())

        // Act
        val response = imageDataSource.uploadImage(ACCESS_TOKEN, image)

        // Assert
        response.isSuccessful shouldBeEqualTo true
        response.body()?.apply {
            this shouldNotBeEqualTo null
            this shouldBeInstanceOf UploadSuccess::class
        }
        coVerify(exactly = 1) { imageApi.postImage(any(), any()) }
        slot.captured.headers?.get("Content-Disposition") shouldBeEqualTo "form-data; name=\"file\"; filename=\"image.jpg\""
    }

    @Test
    fun `uploadImage return error when imageApi response failed`() = runBlocking {
        // Arrange
        val image = byteArrayOf(1, 2, 3)
        coEvery { imageApi.postImage(any(), any()) } returns Response.error(401, "error".toResponseBody())

        // Act
        val response = imageDataSource.uploadImage(ACCESS_TOKEN, image)

        // Assert
        response.isSuccessful shouldBeEqualTo false
        response.errorBody() shouldNotBeEqualTo null
        coVerify(exactly = 1) { imageApi.postImage(any(), any()) }
    }

    companion object {
        private const val ACCESS_TOKEN = "test_token"
    }
}