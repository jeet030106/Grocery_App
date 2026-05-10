package com.example.grocery_app.ui.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocery_app.data.data_store.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val mobileNumber: String = "",
    val otp: String = "",
    val isOtpSent: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(private val userPreferences: UserPreferences) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onMobileNumberChange(newNumber: String) {
        if (newNumber.all { it.isDigit() } && newNumber.length <= 10) {
            _uiState.update { it.copy(mobileNumber = newNumber, errorMessage = null) }
        }
    }

    fun onOtpChange(newOtp: String) {
        if (newOtp.all { it.isDigit() } && newOtp.length <= 4) {
            _uiState.update { it.copy(otp = newOtp, errorMessage = null) }
        }
    }

    fun onSendOtpClick() {
        val currentNumber = _uiState.value.mobileNumber
        if (currentNumber.length == 10) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                delay(1000)
                _uiState.update { it.copy(isLoading = false, isOtpSent = true) }
            }
        } else {
            _uiState.update { it.copy(errorMessage = "Please enter a valid 10-digit mobile number") }
        }
    }

    fun onVerifyOtpClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(1000)

            if (_uiState.value.otp == "1234") {
                userPreferences.saveLoginState(true)
                _uiState.update { it.copy(isLoading = false, isLoginSuccessful = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Invalid OTP. Please use 1234.",
                        otp = ""
                    )
                }
            }
        }
    }
}