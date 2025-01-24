package com.example.imagegallery.data.repository

import com.example.imagegallery.MainDispatcherRule
import com.example.imagegallery.data.common.Result
import com.example.imagegallery.data.common.getErrorMsg
import com.example.imagegallery.data.source.local.TokenLocalDataSource
import com.example.imagegallery.data.source.remote.auth.TokenRemoteDataSource
import com.example.imagegallery.data.source.remote.auth.model.TokenResponseModel
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class TokenRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var tokenRemoteDataSource: TokenRemoteDataSource

    @MockK
    private lateinit var tokenLocalDataSource: TokenLocalDataSource

    private lateinit var tokenRepository: TokenRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        tokenRepository =
            TokenRepositoryImpl(tokenRemoteDataSource, tokenLocalDataSource, Dispatchers.IO)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `login successfully and save tokens`() =
        runBlocking {

            val response = mockk<Response<TokenResponseModel>> {
                every { isSuccessful } returns true
                every { body() } returns TokenResponseModel(true, ACCESS_TOKEN, REFRESH_TOKEN)
            }

            coEvery { tokenRemoteDataSource.login(any()) } returns response
            coEvery { tokenLocalDataSource.saveTokens(any(), any()) } just Runs

            val result = tokenRepository.login(USER_NAME, PWD)

            result shouldBeInstanceOf Result.Success::class
            (result as Result.Success).data shouldBeEqualTo ACCESS_TOKEN
            coVerify(exactly = 1) { tokenLocalDataSource.saveTokens(any(), any()) }
            coVerify(exactly = 1) { tokenRemoteDataSource.login(any()) }
        }

    @Test
    fun `when login failed, return error`() = runBlocking {

        coEvery { tokenRemoteDataSource.login(any()) } returns Response.error(
            404,
            ERROR_MSG.toResponseBody(null)
        )

        val result = tokenRepository.login(USER_NAME, PWD)

        result `should be instance of` Result.Error::class
        result.getErrorMsg() shouldBeEqualTo ERROR_MSG
        coVerify(exactly = 0) { tokenLocalDataSource.saveTokens(any(), any()) }
        coVerify(exactly = 1) { tokenRemoteDataSource.login(any()) }
    }

    @Test
    fun `when login catch exception, return error`() = runBlocking {

        coEvery { tokenRemoteDataSource.login(any()) } throws Exception(ERROR_MSG)

        val result = tokenRepository.login(USER_NAME, PWD)

        result `should be instance of` Result.Error::class
        result.getErrorMsg() shouldBeEqualTo ERROR_MSG
        coVerify(exactly = 0) { tokenLocalDataSource.saveTokens(any(), any()) }
        coVerify(exactly = 1) { tokenRemoteDataSource.login(any()) }
    }

    @Test
    fun `get access token successfully`() = runBlocking {
        coEvery { tokenLocalDataSource.getAccessToken() } returns ACCESS_TOKEN

        val result = tokenRepository.getToken()

        result shouldBeInstanceOf Result.Success::class
        (result as Result.Success).data shouldBeEqualTo ACCESS_TOKEN
        coVerify(exactly = 1) { tokenLocalDataSource.getAccessToken() }
    }


    @Test
    fun `getToken should return error when access token is null`() = runBlocking {
        coEvery { tokenLocalDataSource.getAccessToken() } returns null

        val result = tokenRepository.getToken()

        assertEquals(Result.Error("Access token is null"), result)
        coVerify(exactly = 1) { tokenLocalDataSource.getAccessToken() }
    }

    @Test
    fun `save tokens successfully`() = runBlocking {
        coEvery { tokenLocalDataSource.saveTokens(any(), any()) } just Runs

        tokenRepository.saveToken(ACCESS_TOKEN, REFRESH_TOKEN)

        coVerify(exactly = 1) { tokenLocalDataSource.saveTokens(ACCESS_TOKEN, REFRESH_TOKEN) }
    }

    @Test
    fun `refreshToken should return success when remote data source returns valid response`() =
        runBlocking {
            val response = mockk<Response<TokenResponseModel>> {
                every { isSuccessful } returns true
                every { body() } returns TokenResponseModel(
                    true,
                    NEW_ACCESS_TOKEN,
                    NEW_REFRESH_TOKEN
                )
            }

            coEvery { tokenLocalDataSource.getRefreshToken() } returns REFRESH_TOKEN
            coEvery { tokenRemoteDataSource.refreshToken(any()) } returns response
            coEvery { tokenLocalDataSource.saveTokens(any(), any()) } just Runs

            val result = tokenRepository.refreshToken()

            assertEquals(Result.Success(NEW_ACCESS_TOKEN), result)
            coVerify (exactly = 1){ tokenLocalDataSource.getRefreshToken() }
            coVerify(exactly = 1){ tokenRemoteDataSource.refreshToken(any()) }
            coVerify(exactly = 1) { tokenLocalDataSource.saveTokens(any(), any()) }
        }

    @Test
    fun `refreshToken should return error when refresh token is null`() = runBlocking {
        coEvery { tokenLocalDataSource.getRefreshToken() } returns null

        val result = tokenRepository.refreshToken()

        assertEquals(Result.Error("Refresh token is null"), result)
        coVerify (exactly = 1){ tokenLocalDataSource.getRefreshToken() }
        coVerify(exactly = 0){ tokenRemoteDataSource.refreshToken(any()) }
    }

    @Test
    fun `refreshToken should return error when TokenRemoteDataSource returns error`() = runBlocking {
        coEvery { tokenLocalDataSource.getRefreshToken() } returns REFRESH_TOKEN
        coEvery { tokenRemoteDataSource.refreshToken(any()) } returns Response.error(
            401,
            ERROR_MSG.toResponseBody(null)
        )

        val result = tokenRepository.refreshToken()

        result `should be instance of` Result.Unauthorized::class
        coVerify (exactly = 1){ tokenLocalDataSource.getRefreshToken() }
        coVerify(exactly = 1){ tokenRemoteDataSource.refreshToken(any()) }
        coVerify(exactly = 0) { tokenLocalDataSource.saveTokens(any(), any()) }
    }

    @Test
    fun `refreshToken should return error when TokenRemoteDataSource throws exception`() = runBlocking {
        coEvery { tokenLocalDataSource.getRefreshToken() } returns REFRESH_TOKEN
        coEvery { tokenRemoteDataSource.refreshToken(any()) } throws Exception(ERROR_MSG)

        val result = tokenRepository.refreshToken()

        result `should be instance of` Result.Error::class
        result.getErrorMsg() shouldBeEqualTo ERROR_MSG
        coVerify (exactly = 1){ tokenLocalDataSource.getRefreshToken() }
        coVerify(exactly = 1){ tokenRemoteDataSource.refreshToken(any()) }
        coVerify(exactly = 0) { tokenLocalDataSource.saveTokens(any(), any()) }
    }

    companion object {
        private const val USER_NAME = "testUser"
        private const val PWD = "testPass"
        private const val ACCESS_TOKEN = "accessToken"
        private const val REFRESH_TOKEN = "refreshToken"
        private const val NEW_ACCESS_TOKEN = "newAccessToken"
        private const val NEW_REFRESH_TOKEN = "newRefreshToken"
        private const val ERROR_MSG = "error"
    }
}