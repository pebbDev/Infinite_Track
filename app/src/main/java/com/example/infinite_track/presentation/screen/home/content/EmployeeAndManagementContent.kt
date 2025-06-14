package com.example.infinite_track.presentation.screen.home.content

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.infinite_track.R
import com.example.infinite_track.data.soucre.network.response.AttendanceHistoryResponseItem
import com.example.infinite_track.data.soucre.network.response.LeaveHistoryResponse
import com.example.infinite_track.domain.model.AhistoryModel
import com.example.infinite_track.domain.model.TimeOffModel
import com.example.infinite_track.presentation.components.button.SeeAllButton
import com.example.infinite_track.presentation.components.cards.AttendanceHistoryC
import com.example.infinite_track.presentation.components.cards.MenuCard
import com.example.infinite_track.presentation.components.cards.TimeOffCard
import com.example.infinite_track.presentation.components.cards.TopAttendanceCard
import com.example.infinite_track.presentation.components.empty.EmptyListAnimation
import com.example.infinite_track.presentation.components.loading.LoadingAnimation
import com.example.infinite_track.presentation.components.tittle.Location
import com.example.infinite_track.presentation.core.headline4
import com.example.infinite_track.utils.UiState
import com.example.infinite_track.utils.calculateTotalCourse
import com.example.infinite_track.utils.fetchLocation
import com.example.infinite_track.utils.formatDate
import com.example.infinite_track.utils.getCurrentDate
import com.example.infinite_track.utils.isInternetAvailable

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EmployeeAndManagerComponent(
    modifier: Modifier = Modifier,
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
    navigateAttendance: () -> Unit,
    navigateTimeOffRequest: () -> Unit,
    navigateListTimeOff: () -> Unit,
    navigateListMyAttendance: () -> Unit,
) {
    val context = LocalContext.current
    val user by employeeViewModel.user.observeAsState()
    var currentLocation by remember { mutableStateOf("Loading...") }
    var leaveHistory: LeaveHistoryResponse? = null

    val leaveHistoryState by employeeViewModel.leaveHistoryState.collectAsState()
    val attendanceState by employeeViewModel.attendanceHistoryRepositoryState.collectAsState()

    val isLoading = leaveHistoryState is UiState.Loading || attendanceState is UiState.Loading

    // Fetch location once
    LaunchedEffect(currentLocation) {
        fetchLocation(context) { location ->
            currentLocation = location
        }
    }

    LaunchedEffect(Unit) {
        if (isInternetAvailable(context)) {
            employeeViewModel.getAllAttendance()
            employeeViewModel.getAllLeaveHistory()
        } else {
            employeeViewModel.setError("Tidak ada koneksi internet")
        }
    }

    user?.let { userData ->
        val annualLeft = employeeViewModel.getAnnualLeft(userData)

        val fullImageUrl = if (!userData.profilePhoto.isNullOrEmpty()) {
            userData.profilePhoto
        } else {
            "https://w7.pngwing.com/pngs/177/551/png-transparent-user-interface-design-computer-icons-default-stephen-salazar-graphy-user-interface-design-computer-wallpaper-sphere-thumbnail.png"
        }

        Box(
            modifier = modifier.fillMaxSize()
        ) {
            Column(
                modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Lokasi
                Location(date = getCurrentDate(), location = currentLocation)

                Spacer(modifier.height(12.dp))

                // Kartu Menu
                MenuCard(
                    annualBalance = userData.annualBalance,
                    annualUsed = userData.annualUsed,
                    annualLeft = annualLeft,
                    userName = userData.userName,
                    position = userData.positionName,
                    greeting = userData.greeting,
                    onClickLiveAttendance = navigateAttendance,
                    onClickTimeOff = navigateTimeOffRequest,
                    profileImage = fullImageUrl
                )

                Spacer(modifier.height(12.dp))

                // Loading (jika sedang memuat data)
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingAnimation()
                    }
                } else {
                    // Tombol lihat semua cuti
                    SeeAllButton(
                        label = stringResource(R.string.list_time_off),
                        onClickButton = navigateListTimeOff
                    )

                    TopAttendanceCard(nomor = "1")
                    TopAttendanceCard(nomor = "2")
                    TopAttendanceCard(nomor = "3")

                    Spacer(modifier.height(8.dp))

                    // Tombol lihat semua cuti
                    SeeAllButton(
                        label = stringResource(R.string.list_time_off),
                        onClickButton = navigateListTimeOff
                    )

                    Spacer(modifier.height(8.dp))

                    // Data Cuti
                    when (leaveHistoryState) {
                        is UiState.Success -> {
                            val leaveHistory = (leaveHistoryState as UiState.Success<LeaveHistoryResponse>).data
                            leaveHistory.dataLeave?.filterNotNull()
                                ?.sortedByDescending { it.submittedAt }
                                ?.take(3)
                                ?.map { dataLeave ->
                                    TimeOffModel(
                                        id = dataLeave.leaveId ?: 0,
                                        name = dataLeave.userName ?: "",
                                        division = dataLeave.division ?: "-",
                                        leaveStartDate = formatDate(dataLeave.startDate ?: "-"),
                                        leaveEndDate = formatDate(dataLeave.endDate ?: "-"),
                                        submittedAt = dataLeave.submittedAt ?: "-",
                                        photoProfile = dataLeave.profilePhoto
                                    )
                                }?.forEach { timeOff ->
                                    TimeOffCard(timeOff = timeOff)
                                }
                        }

                        is UiState.Error -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                EmptyListAnimation(modifier = Modifier.size(150.dp))
                                Text(
                                    text = (leaveHistoryState as UiState.Error).errorMessage,
                                    style = headline4,
                                )
                            }
                        }

                        else -> Unit
                    }

                    Spacer(modifier.height(12.dp))

                    // Tombol lihat semua absensi
                    SeeAllButton(
                        label = stringResource(R.string.attendance_history),
                        onClickButton = navigateListMyAttendance
                    )

                    Spacer(modifier.height(12.dp))

                    // Data Absensi
                    when (attendanceState) {
                        is UiState.Success -> {
                            val topFiveAttendance = (attendanceState as UiState.Success<List<AttendanceHistoryResponseItem>>).data
                                .sortedByDescending {
                                    it.attendanceId ?: 0
                                }
                                .take(5)

                            topFiveAttendance.forEach { attendance ->
                                AttendanceHistoryC(
                                    AhistoryModel(
                                        id = attendance.attendanceId ?: 0,
                                        date = attendance.attendanceDate ?: "N/A",
                                        monthYear = attendance.attendanceMonthYear ?: "N/A",
                                        checkIn = attendance.checkInTime ?: "N/A",
                                        checkOut = attendance.checkOutTime ?: "--:--",
                                        totalCourse = calculateTotalCourse(
                                            attendance.checkInTime,
                                            attendance.checkOutTime
                                        )
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        is UiState.Error -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    EmptyListAnimation(modifier = Modifier.size(150.dp))
                                    Text(
                                        text = (attendanceState as UiState.Error).errorMessage,
                                        style = headline4,
                                )
                            }
                        }
                    }
                    else -> Unit
                    }
                }
            }
        }
    }
}