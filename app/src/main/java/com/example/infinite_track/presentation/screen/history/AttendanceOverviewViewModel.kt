package com.example.infinite_track.presentation.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infinite_track.data.soucre.network.response.AttendanceOverviewData
import com.example.infinite_track.data.soucre.network.response.OverviewData
import com.example.infinite_track.data.soucre.repository.Attendance.AttendanceOverviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceOverviewViewModel @Inject constructor(
    private val attendanceOverviewRepository: AttendanceOverviewRepository
) : ViewModel() {

    private val _overviewData = MutableStateFlow<List<AttendanceOverviewData>>(emptyList())
    val overviewData: StateFlow<List<AttendanceOverviewData>> = _overviewData

    private val _allData = MutableStateFlow(OverviewData())
    val allData: StateFlow<OverviewData>
        get() = _allData

    fun fetchAttendanceOverview() {
        viewModelScope.launch {
            attendanceOverviewRepository.getAttendanceOverview()
                .collect { response ->
                    response.overviewData?.let { data ->
                        val overviewList = listOf(
                            AttendanceOverviewData("Total Attend", data.totalAttendance?: 0, "Days"),
                            AttendanceOverviewData("Late", data.late?.toIntOrNull()?: 0, "Days"),
                            AttendanceOverviewData("Work From Home", data.totalWFH?.toIntOrNull()?: 0, "Days"),
                            AttendanceOverviewData("Work From Office", data.totalWFO?.toIntOrNull()?: 0, "Days")
                        )
                        _overviewData.value = overviewList
                    }
                }
        }
    }
}
