package com.example.infinite_track.presentation.screen.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.infinite_track.data.soucre.network.response.UserResponse
import com.example.infinite_track.data.soucre.repository.auth.AuthRepository
import com.example.infinite_track.domain.model.userdata.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    fun getUser(): LiveData<UserModel> = authRepository.getUser().asLiveData()
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}