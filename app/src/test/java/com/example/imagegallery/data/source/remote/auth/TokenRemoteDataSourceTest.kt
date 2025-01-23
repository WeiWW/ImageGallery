package com.example.imagegallery.data.source.remote.auth

import com.example.imagegallery.MainDispatcherRule
import com.example.imagegallery.data.source.remote.auth.model.LoginApiModel
import com.example.imagegallery.data.source.remote.auth.model.TokenResponseModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class TokenRemoteDataSourceTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var tokenRemoteDataSource: TokenRemoteDataSource

    @MockK
    private lateinit var authApi: AuthApi

    @MockK
    private lateinit var loginApiModel: LoginApiModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        tokenRemoteDataSource = TokenRemoteDataSourceImpl(authApi)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `when login successfully, return accessToken & refreshToken`() = runTest {
        // Given
        coEvery { authApi.login(any()) } returns Response.success(
            TokenResponseModel(true, "accessToken", "refreshToken")
        )

        // When
        val result = tokenRemoteDataSource.login(loginApiModel)

        // Then
        result.body().apply {
            this shouldNotBeEqualTo null
            this?.success shouldBeEqualTo true
            this?.accessToken?.isNotEmpty() shouldBeEqualTo true
            this?.refreshToken?.isNotEmpty() shouldBeEqualTo true
        }
        coVerify(exactly = 1) { authApi.login(any()) }
    }

    @Test
    fun `when login failed, return error`() = runTest {
        // Given
        coEvery { authApi.login(any()) } returns Response.error(
            401,
            "{\"success\":false,\"error\":\"Invalidusernameorpassword\"}".toResponseBody()
        )

        // When
        val result = tokenRemoteDataSource.login(loginApiModel)

        // Then
        result.isSuccessful shouldBeEqualTo false
        result.errorBody()
            ?.string() shouldBeEqualTo "{\"success\":false,\"error\":\"Invalidusernameorpassword\"}"
        coVerify(exactly = 1) { authApi.login(any()) }
    }

    @Test
    fun `when refresh token successfully, return accessToken & refreshToken`() = runTest {
        // Given
        coEvery { authApi.refreshToken(any()) } returns Response.success(
            TokenResponseModel(
                true,
                "newAccessToken",
                "newRefreshToken"
            )
        )

        // When
        val result = tokenRemoteDataSource.refreshToken("refreshToken")

        // Then
        result.body().apply {
            this shouldNotBeEqualTo null
            this?.success shouldBeEqualTo true
            this?.accessToken?.isNotEmpty() shouldBeEqualTo true
            this?.refreshToken?.isNotEmpty() shouldBeEqualTo true
        }
        coVerify(exactly = 1) { authApi.refreshToken(any()) }
    }

    @Test
    fun `when refresh token failed, return error`() = runTest {
        // Given
        coEvery { authApi.refreshToken(any()) } returns Response.error(
            401,
            "{\"success\":false,\"error\":\"InvalidrefreshToken\"}".toResponseBody()
        )

        // When
        val result = tokenRemoteDataSource.refreshToken("refreshToken")

        // Then
        result.isSuccessful shouldBeEqualTo false
        result.errorBody()
            ?.string() shouldBeEqualTo "{\"success\":false,\"error\":\"InvalidrefreshToken\"}"
        coVerify(exactly = 1) { authApi.refreshToken(any()) }
    }
}