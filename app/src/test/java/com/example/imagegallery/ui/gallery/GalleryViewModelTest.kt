package com.example.imagegallery.ui.gallery

import android.content.Context
import android.net.Uri
import com.example.imagegallery.MainDispatcherRule
import com.example.imagegallery.data.common.Result
import com.example.imagegallery.data.repository.image.ImageRepository
import com.example.imagegallery.data.source.remote.image.model.Image
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBe
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GalleryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var imageRepository: ImageRepository

    @MockK(relaxed = true)
    private lateinit var context: Context

    @MockK
    private lateinit var uri: Uri

    private lateinit var viewModel: GalleryViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `fetchImages success when ImageRepository return success`() {
        // Arrange
        coEvery { imageRepository.getImages() } returns Result.Success(mockk<List<Image>>(relaxed = true))

        // Act
        viewModel = GalleryViewModel(imageRepository)

        // Assert
        viewModel.uiState.value.images shouldBeInstanceOf List::class
        viewModel.uiState.value.isLoading shouldBe false
        viewModel.uiState.value.error.shouldBeNull()
    }

    @Test
    fun `fetchImages error when ImageRepository return error`() {
        // Arrange
        val errorMessage = "Failed to fetch images"
        coEvery { imageRepository.getImages() } returns Result.Error(errorMessage)

        // Act
        viewModel = GalleryViewModel(imageRepository)

        // Assert
        viewModel.uiState.value.images shouldBeEqualTo emptyList()
        viewModel.uiState.value.isLoading shouldBe false
        viewModel.uiState.value.error shouldBeEqualTo errorMessage
    }

    @Test
    fun `updateImage success when ImageRepository return success`() {
        coEvery { imageRepository.getImages() } returns Result.Success(mockk<List<Image>>(relaxed = true))

        // Mock the convertUriToByteArray method
        val spyViewModel = spyk(GalleryViewModel(imageRepository)) {
            coEvery { convertUriToByteArray(context, uri) } returns ByteArray(10)
        }

        coEvery { imageRepository.uploadImage(any()) } returns Result.Success(Unit)

        // Act
        spyViewModel.updateImage(context, uri)

        coVerify(exactly = 1) { spyViewModel.fetchImages() }
        coVerify(exactly = 1) { spyViewModel.convertUriToByteArray(any(), any()) }
        coVerify(exactly = 1) { imageRepository.uploadImage(any()) }
        spyViewModel.uiState.value.isLoading shouldBe false
        spyViewModel.uiState.value.error.shouldBeNull()
        spyViewModel.uiState.value.images.shouldBeInstanceOf(List::class)
    }

    @Test
    fun `updateImage error when convert to byte array failed`() {
        coEvery { imageRepository.getImages() } returns Result.Success(mockk<List<Image>>(relaxed = true))

        // Mock the convertUriToByteArray method
        val spyViewModel = spyk(GalleryViewModel(imageRepository)) {
            coEvery { convertUriToByteArray(context, uri) } returns null
        }

        spyViewModel.updateImage(context, uri)

        coVerify(exactly = 1) { spyViewModel.convertUriToByteArray(any(), any()) }
        coVerify(exactly = 0) { imageRepository.uploadImage(any()) }
        coVerify(exactly = 0) { spyViewModel.fetchImages() }
        spyViewModel.uiState.value.isLoading shouldBe false
        spyViewModel.uiState.value.error shouldNotBe null
    }

    @Test
    fun `updateImage failed when upload image failed`() {
        imageRepository = mockk {
            coEvery { getImages() } returns Result.Success(mockk<List<Image>>(relaxed = true))
            coEvery { uploadImage(any()) } returns Result.Error("Failed to upload image")
        }

        val spyViewModel = spyk(GalleryViewModel(imageRepository)) {
            coEvery { convertUriToByteArray(context, uri) } returns ByteArray(10)
        }

        spyViewModel.updateImage(context, uri)
        coVerify(exactly = 1) { spyViewModel.convertUriToByteArray(any(), any()) }
        coVerify(exactly = 1) { imageRepository.uploadImage(any()) }
        coVerify(exactly = 0) { spyViewModel.fetchImages() }
        spyViewModel.uiState.value.isLoading shouldBe false
        spyViewModel.uiState.value.error shouldNotBe null
    }

}