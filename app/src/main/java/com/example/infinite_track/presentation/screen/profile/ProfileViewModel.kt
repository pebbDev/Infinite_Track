package com.example.infinite_track.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infinite_track.data.soucre.NetworkResponse
import com.example.infinite_track.data.soucre.network.request.ProfileRequest
import com.example.infinite_track.data.soucre.network.response.ProfileResponse
import com.example.infinite_track.data.soucre.repository.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<NetworkResponse<ProfileResponse>>(NetworkResponse.Idle)
    val profileState: StateFlow<NetworkResponse<ProfileResponse>> = _profileState

    fun updateProfile(
        userId: Int,
        phone_number: String?,
        nip_nim: String?,
        address: String?,
        start_contract: String?,
        end_contract: String?
    ) {
        viewModelScope.launch {
            _profileState.value = NetworkResponse.Loading
            try {
                val profileRequest = ProfileRequest(
                    phone_number = phone_number?:"",
                    nip_nim = nip_nim?:"",
                    address = address?:"",
                    start_contract = start_contract,
                    end_contract = end_contract
                )
                val response = profileRepository.updateUserProfile(userId, profileRequest)
                _profileState.value = NetworkResponse.Success(response)
            } catch (e: Exception) {
                _profileState.value = NetworkResponse.Error(e.message ?: "Unexpected error occurred")
            }
        }
    }
}

