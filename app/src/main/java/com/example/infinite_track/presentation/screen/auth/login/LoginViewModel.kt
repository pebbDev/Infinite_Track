package com.example.infinite_track.presentation.screen.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infinite_track.data.soucre.network.request.LoginRequest
import com.example.infinite_track.data.soucre.network.response.UserResponse
import com.example.infinite_track.data.soucre.repository.auth.AuthRepository
import com.example.infinite_track.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiLoginState: MutableStateFlow<UiState<UserResponse>> =
        MutableStateFlow(UiState.Idle)
    val uiLoginState: StateFlow<UiState<UserResponse>> get() = _uiLoginState

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            _uiLoginState.value = UiState.Loading
            try {
                authRepository.login(loginRequest).collect { response ->
                    _uiLoginState.value = UiState.Success(response)
                }
            } catch (e: Exception) {
                _uiLoginState.value = UiState.Error(e.message ?: "Unexpected error occurred")
            }
        }
    }
}
