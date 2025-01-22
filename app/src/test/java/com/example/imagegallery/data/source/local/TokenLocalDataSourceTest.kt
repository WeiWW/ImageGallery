package com.example.imagegallery.data.source.local

import com.example.imagegallery.MainDispatcherRule
import com.example.imagegallery.data.source.local.TokenLocalDataSourceImpl.Companion.ACCESS_TOKEN_KEY
import com.example.imagegallery.data.source.local.TokenLocalDataSourceImpl.Companion.REFRESH_TOKEN_KEY
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TokenLocalDataSourceImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var secureTokenManager: SecureTokenManager

    private lateinit var tokenLocalDataSource: TokenLocalDataSourceImpl
    private val accessToken = "accessToken"
    private val refreshToken = "refreshToken"

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        tokenLocalDataSource = TokenLocalDataSourceImpl(secureTokenManager)
    }

    @Test
    fun `get access token successfully from SecureTokenManager`() {
        // Given
        every { secureTokenManager.getToken(any()) } returns accessToken

        // When
        val result = tokenLocalDataSource.getAccessToken()

        // Then
        result shouldNotBeEqualTo  null
        result shouldBeEqualTo accessToken
        verify(exactly = 1) { secureTokenManager.getToken(any()) }
    }

    @Test
    fun `get access token as null from SecureTokenManager`() {
        // Given
        every { secureTokenManager.getToken(any()) } returns null

        // When
        val result = tokenLocalDataSource.getAccessToken()

        // Then
        result shouldBeEqualTo null
        verify(exactly = 1) { secureTokenManager.getToken(any()) }
    }

    @Test
    fun `get refresh token successfully from SecureTokenManager`() {
        // Given
        every { secureTokenManager.getToken(any()) } returns refreshToken

        // When
        val result = tokenLocalDataSource.getRefreshToken()

        // Then
        result shouldNotBeEqualTo  null
        result shouldBeEqualTo refreshToken
        verify(exactly = 1) { secureTokenManager.getToken(any()) }
    }

    @Test
    fun `get refresh token as null from SecureTokenManager`() {
        // Given
        every { secureTokenManager.getToken(any()) } returns null

        // When
        val result = tokenLocalDataSource.getRefreshToken()

        // Then
        result shouldBeEqualTo null
        verify(exactly = 1) { secureTokenManager.getToken(any()) }
    }

    @Test
    fun `saveTokens should save access and refresh tokens`() {
        // Given
        every { secureTokenManager.saveToken(any(), any()) } returns Unit

        // When
        tokenLocalDataSource.saveTokens(accessToken, refreshToken)

        // Then
        verify(exactly = 2) { secureTokenManager.saveToken(any(),any()) }
    }
}