package com.example.imagegallery.data.repository.image

import com.example.imagegallery.MainDispatcherRule
import com.example.imagegallery.data.common.Result
import com.example.imagegallery.data.repository.token.TokenRepository
import com.example.imagegallery.data.source.remote.image.ImageDataSource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ImageRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var imageRepository: ImageRepositoryImpl

    @MockK
    private lateinit var tokenRepository: TokenRepository

    @MockK
    private lateinit var imageDataSource: ImageDataSource

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        imageRepository = ImageRepositoryImpl(tokenRepository, imageDataSource, Dispatchers.IO)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getImage returns error when tokenRepository returns error`() = runBlocking {
        coEvery { tokenRepository.getToken() } returns Result.Error("error")

        val result = imageRepository.getImages()

        result shouldBeInstanceOf Result.Error::class
        coVerify(exactly = 1) { tokenRepository.getToken() }
        coVerify(exactly = 0) { imageDataSource.getImages(any()) }
        coVerify(exactly = 0) { tokenRepository.refreshToken() }
    }

    @Test
    fun `getImages returns success when imageDataSource responds successfully`() = runBlocking {
        coEvery { tokenRepository.getToken() } returns Result.Success(ACCESS_TOKEN)
        coEvery { imageDataSource.getImages(any()) } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns mockk {
                every { images } returns listOf(mockk())
            }
        }

        val result = imageRepository.getImages()

        result shouldBeInstanceOf Result.Success::class
        (result as Result.Success).data shouldBeInstanceOf List::class
        coVerify(exactly = 1) { tokenRepository.getToken() }
        coVerify(exactly = 1) { imageDataSource.getImages(any()) }
        coVerify(exactly = 0) { tokenRepository.refreshToken() }
    }

    @Test
    fun `getImages returns error when imageDataSource responds with error`() = runBlocking {
        coEvery { tokenRepository.getToken() } returns Result.Success(ACCESS_TOKEN)
        coEvery { imageDataSource.getImages(any()) } returns mockk {
            every { isSuccessful } returns false
            every { code() } returns 500
        }

        val result = imageRepository.getImages()

        result shouldBeInstanceOf Result.Error::class
        coVerify(exactly = 1) { tokenRepository.getToken() }
        coVerify(exactly = 1) { imageDataSource.getImages(any()) }
        coVerify(exactly = 0) { tokenRepository.refreshToken() }
    }

    @Test
    fun `getImages returns success after refresh token`() = runBlocking {
        coEvery { tokenRepository.getToken() } returns Result.Success(ACCESS_TOKEN)
        coEvery { imageDataSource.getImages(any()) } returnsMany listOf(
            mockk {
                every { isSuccessful } returns false
                every { code() } returns 401
            },
            mockk {
                every { isSuccessful } returns true
                every { body() } returns mockk {
                    every { images } returns listOf(mockk())
                }
            }
        )
        coEvery { tokenRepository.refreshToken() } returns Result.Success("new_access_token")

        val result = imageRepository.getImages()

        result shouldBeInstanceOf Result.Success::class
        (result as Result.Success).data shouldBeInstanceOf List::class
        coVerify(exactly = 1) { tokenRepository.getToken() }
        coVerify(exactly = 2) { imageDataSource.getImages(any()) }
        coVerify(exactly = 1) { tokenRepository.refreshToken() }
    }

    @Test
    fun `getImage returns error when refresh token failure`() = runBlocking {
        coEvery { tokenRepository.getToken() } returns Result.Success(ACCESS_TOKEN)
        coEvery { imageDataSource.getImages(any()) } returns mockk {
            every { isSuccessful } returns false
            every { code() } returns 401
        }
        coEvery { tokenRepository.refreshToken() } returns Result.Error("error")

        val result = imageRepository.getImages()

        result shouldBeInstanceOf Result.Error::class
        coVerify(exactly = 1) { tokenRepository.getToken() }
        coVerify(exactly = 1) { imageDataSource.getImages(any()) }
        coVerify(exactly = 1) { tokenRepository.refreshToken() }
    }

    @Test
    fun `getImages returns error even after refresh token`() = runBlocking {
        coEvery { tokenRepository.getToken() } returns Result.Success(ACCESS_TOKEN)
        coEvery { imageDataSource.getImages(any()) } returnsMany listOf(
            mockk {
                every { isSuccessful } returns false
                every { code() } returns 401
            },
            mockk {
                every { isSuccessful } returns false
                every { code() } returns 500
            }
        )
        coEvery { tokenRepository.refreshToken() } returns Result.Success("new_access_token")

        val result = imageRepository.getImages()

        result shouldBeInstanceOf Result.Error::class
        coVerify(exactly = 1) { tokenRepository.getToken() }
        coVerify(exactly = 2) { imageDataSource.getImages(any()) }
        coVerify(exactly = 1) { tokenRepository.refreshToken() }
    }

    @Test
    fun `uploadImage return error when tokenRepository returns error`() = runBlocking {
        coEvery { tokenRepository.getToken() } returns Result.Error("error")

        val result = imageRepository.uploadImage(IMAGE)

        result shouldBeInstanceOf Result.Error::class
        coVerify(exactly = 1) { tokenRepository.getToken() }
        coVerify(exactly = 0) { imageDataSource.uploadImage(any(), any()) }
    }

    @Test
    fun `uploadImage returns success when imageDataSource responds successfully`() = runBlocking {
        coEvery { tokenRepository.getToken() } returns Result.Success(ACCESS_TOKEN)
        coEvery { imageDataSource.uploadImage(any(), any()) } returns mockk {
            every { isSuccessful } returns true
        }

        val result = imageRepository.uploadImage(IMAGE)

        result shouldBeInstanceOf Result.Success::class
        coVerify(exactly = 1) { tokenRepository.getToken() }
        coVerify(exactly = 1) { imageDataSource.uploadImage(any(), any()) }
        coVerify(exactly = 0) { tokenRepository.refreshToken() }
    }

    @Test
    fun `uploadImage returns error when imageDataSource responds with error`() = runBlocking {
        coEvery { tokenRepository.getToken() } returns Result.Success(ACCESS_TOKEN)
        coEvery { imageDataSource.uploadImage(any(), any()) } returns mockk {
            every { isSuccessful } returns false
            every { code() } returns 500
        }

        val result = imageRepository.uploadImage(IMAGE)

        result shouldBeInstanceOf Result.Error::class
        coVerify(exactly = 1) { tokenRepository.getToken() }
        coVerify(exactly = 1) { imageDataSource.uploadImage(any(), any()) }
        coVerify(exactly = 0) { tokenRepository.refreshToken() }
    }

    @Test
    fun `uploadImage return success after refresh token`() = runBlocking {
        coEvery { tokenRepository.getToken() } returns Result.Success(ACCESS_TOKEN)
        coEvery { imageDataSource.uploadImage(any(), any()) } returnsMany listOf(
            mockk {
                every { isSuccessful } returns false
                every { code() } returns 401
            },
            mockk {
                every { isSuccessful } returns true
            }
        )
        coEvery { tokenRepository.refreshToken() } returns Result.Success("new_access_token")

        val result = imageRepository.uploadImage(IMAGE)

        result shouldBeInstanceOf Result.Success::class
        coVerify(exactly = 1) { tokenRepository.getToken() }
        coVerify(exactly = 2) { imageDataSource.uploadImage(any(), any()) }
        coVerify(exactly = 1) { tokenRepository.refreshToken() }
    }

    @Test
    fun `uploadImage returns error even after refresh token`() = runBlocking {
        coEvery { tokenRepository.getToken() } returns Result.Success(ACCESS_TOKEN)
        coEvery { imageDataSource.uploadImage(any(), any()) } returnsMany listOf(
            mockk {
                every { isSuccessful } returns false
                every { code() } returns 401
            },
            mockk {
                every { isSuccessful } returns false
                every { code() } returns 500
            }
        )
        coEvery { tokenRepository.refreshToken() } returns Result.Success("new_access_token")

        val result = imageRepository.uploadImage(IMAGE)

        result shouldBeInstanceOf Result.Error::class
        coVerify(exactly = 1) { tokenRepository.getToken() }
        coVerify(exactly = 2) { imageDataSource.uploadImage(any(), any()) }
        coVerify(exactly = 1) { tokenRepository.refreshToken() }
    }

    companion object {
        private const val ACCESS_TOKEN = "access"
        private val IMAGE = byteArrayOf(1, 2, 3)
    }
}