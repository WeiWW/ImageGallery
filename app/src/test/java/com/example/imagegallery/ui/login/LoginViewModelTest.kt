package com.example.imagegallery.ui.login

import com.example.imagegallery.MainDispatcherRule
import com.example.imagegallery.data.common.Result
import com.example.imagegallery.data.repository.token.TokenRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: LoginViewModel

    @MockK
    private lateinit var tokenRepository: TokenRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = LoginViewModel(tokenRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `onUsernameChange updates username with validation state`() {

        viewModel.onUsernameChange(VALID_USER)

        viewModel.uiState.value.username shouldBeEqualTo VALID_USER
        viewModel.uiState.value.isValidUsrName shouldBe true
    }

    @Test
    fun `onUsernameChange updates username with invalidation state`() {

        viewModel.onUsernameChange(INVALID_USER)

        viewModel.uiState.value.username shouldBeEqualTo INVALID_USER
        viewModel.uiState.value.isValidUsrName shouldBe false
    }

    @Test
    fun `when change username, the username should be updated`() {
        viewModel.onUsernameChange(INVALID_USER)
        viewModel.uiState.value.username shouldBeEqualTo INVALID_USER
        viewModel.uiState.value.isValidUsrName shouldBe false

        viewModel.onUsernameChange(VALID_USER)
        viewModel.uiState.value.username shouldBeEqualTo VALID_USER
        viewModel.uiState.value.isValidUsrName shouldBe true
    }

    @Test
    fun `onPasswordChange updates password and validation state`() {

        viewModel.onPasswordChange(VALID_PASS)

        viewModel.uiState.value.password shouldBeEqualTo VALID_PASS
        viewModel.uiState.value.isValidPwd shouldBe true
    }

    @Test
    fun `onPasswordChange updates password with invalidation state`() {

        viewModel.onPasswordChange(INVALID_PASS)

        viewModel.uiState.value.password shouldBeEqualTo INVALID_PASS
        viewModel.uiState.value.isValidPwd shouldBe false
    }

    @Test
    fun `when change password, the password should be updated`() {
        viewModel.onPasswordChange(INVALID_PASS)
        viewModel.uiState.value.password shouldBeEqualTo INVALID_PASS
        viewModel.uiState.value.isValidPwd shouldBe false

        viewModel.onPasswordChange(VALID_PASS)
        viewModel.uiState.value.password shouldBeEqualTo VALID_PASS
        viewModel.uiState.value.isValidPwd shouldBe true
    }

    @Test
    fun `onLoginClick updates login success when TokenRepository return success`() {

        viewModel.onUsernameChange(VALID_USER)
        viewModel.onPasswordChange(VALID_PASS)

        coEvery { tokenRepository.login(VALID_USER, VALID_PASS) } returns Result.Success("token")

        viewModel.onLoginClick()

        viewModel.uiState.value.loginSuccess shouldBe true
        viewModel.uiState.value.errorMessage shouldBe null
    }

    @Test
    fun `onLoginClick updates login failed when TokenRepository return error`() {
        viewModel.onUsernameChange(VALID_USER)
        viewModel.onPasswordChange(VALID_PASS)

        coEvery {
            tokenRepository.login(
                VALID_USER,
                VALID_PASS
            )
        } returns Result.Error("Login failed. Please try again.")

        viewModel.onLoginClick()

        viewModel.uiState.value.loginSuccess shouldBe false
        viewModel.uiState.value.errorMessage shouldBeEqualTo "Login failed. Please try again."
    }

    companion object {
        private const val VALID_USER = "validUser"
        private const val VALID_PASS = "validPass123"
        private const val INVALID_USER = "invalid"
        private const val INVALID_PASS = "invalid"
    }
}