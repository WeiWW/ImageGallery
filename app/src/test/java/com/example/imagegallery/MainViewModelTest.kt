package com.example.imagegallery

import com.example.imagegallery.data.common.Result
import com.example.imagegallery.data.repository.TokenRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBe
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var tokenRepository: TokenRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `when token exists, hasToken should be true`() = runTest {
        // Given
        coEvery { tokenRepository.getToken() } returns Result.Success("some-token")

        viewModel = MainViewModel(tokenRepository)

        // Then
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        val initialState = viewModel.uiState.first()
        initialState.hasToken shouldBe true
    }

    @Test
    fun `when token doesn't exist, hasToken should be false`() = runTest {
        // Given
        coEvery { tokenRepository.getToken() } returns Result.Error("Token not found")

        // When
        viewModel = MainViewModel(tokenRepository)

        // Then
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        val initialState = viewModel.uiState.first()
        initialState.hasToken shouldBe false
    }
}