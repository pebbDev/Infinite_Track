package com.example.infinite_track.presentation.components.user.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.infinite_track.data.soucre.network.response.AttendanceHistoryResponseItem
import com.example.infinite_track.data.soucre.network.response.AttendanceOverviewResponse
import com.example.infinite_track.data.soucre.network.response.LeaveHistoryResponse
import com.example.infinite_track.data.soucre.repository.Attendance.AttendanceHistoryRepository
import com.example.infinite_track.data.soucre.repository.auth.AuthRepository
import com.example.infinite_track.data.soucre.repository.leave.LeaveRepository
import com.example.infinite_track.domain.model.userdata.UserModel
import com.example.infinite_track.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val attendanceHistoryRepository: AttendanceHistoryRepository,
    private val leaveHistoryRepository: LeaveRepository
) : ViewModel() {

    val user: LiveData<UserModel> = authRepository.getUser().asLiveData()

    fun getAnnualLeft(user: UserModel): Int {
        return (user.annualBalance ?: 0) - (user.annualUsed ?: 0)
    }

    private val _attendanceHistoryRepositoryState: MutableStateFlow<UiState<List<AttendanceHistoryResponseItem>>> =
        MutableStateFlow(UiState.Loading)

    val attendanceHistoryRepositoryState: StateFlow<UiState<List<AttendanceHistoryResponseItem>>>
        get() = _attendanceHistoryRepositoryState

    fun getAllAttendance() {
        viewModelScope.launch {
            _attendanceHistoryRepositoryState.value = UiState.Loading
            delay(1000)

            attendanceHistoryRepository.getAllAttendanceRepository()
                .catch { e ->
                    _attendanceHistoryRepositoryState.value = UiState.Error(e.message.toString())
                }
                .collect { response ->
                    _attendanceHistoryRepositoryState.value = UiState.Success(response)
                }
        }
    }

    //Overview
    private val _overviewData = MutableLiveData<AttendanceOverviewResponse>()
    val overviewData: LiveData<AttendanceOverviewResponse> get() = _overviewData

    fun getOverview() {
        viewModelScope.launch {
            attendanceHistoryRepository.getAllOverviewRepository()
                .collect { overview ->
                    Log.d("AttendanceOverview", "Overview: $overview")
                    _overviewData.postValue(overview)
                }
        }
    }

    //LEAVE HISTORY
    private val _leaveHistoryState: MutableStateFlow<UiState<LeaveHistoryResponse>> =
        MutableStateFlow(UiState.Loading)

    val leaveHistoryState: StateFlow<UiState<LeaveHistoryResponse>>
        get() = _leaveHistoryState

    fun getAllLeaveHistory() {
        viewModelScope.launch {
            _leaveHistoryState.value = UiState.Loading
            delay(1000)
            leaveHistoryRepository.getAllLeave()
                .catch {
                    _leaveHistoryState.value =
                        UiState.Error(it.message.toString())
                }
                .collect { responseList ->
                    _leaveHistoryState.value = UiState.Success(responseList)
                }
        }
    }
    fun setError(message: String) {
        _attendanceHistoryRepositoryState.value = UiState.Error(message)
    }
}




