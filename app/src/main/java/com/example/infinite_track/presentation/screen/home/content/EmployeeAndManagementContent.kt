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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.infinite_track.R
import com.example.infinite_track.domain.model.attendance.AttendanceRecord
import com.example.infinite_track.domain.model.auth.UserModel
import com.example.infinite_track.presentation.components.button.SeeAllButton
import com.example.infinite_track.presentation.components.cards.AttendanceHistoryC
import com.example.infinite_track.presentation.components.cards.MenuCard
import com.example.infinite_track.presentation.components.empty.EmptyListAnimation
import com.example.infinite_track.presentation.components.loading.LoadingAnimation
import com.example.infinite_track.presentation.components.tittle.Location
import com.example.infinite_track.presentation.core.headline4
import com.example.infinite_track.utils.UiState
import com.example.infinite_track.utils.getCurrentDate
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EmployeeAndManagerComponent(
    modifier: Modifier = Modifier,
    user: UserModel?,
    attendanceState: UiState<List<AttendanceRecord>>,
    annualBalance: Int,
    annualUsed: Int,
    currentLocation: String,
    isLoading: Boolean = false,
    navigateAttendance: () -> Unit,
    navigateTimeOffRequest: () -> Unit,
    navigateListMyAttendance: () -> Unit,
) {
    val annualLeft = annualBalance - annualUsed

    user?.let { userData ->
        val fullImageUrl = userData.photoUrl?.ifEmpty {
            "https://w7.pngwing.com/pngs/177/551/png-transparent-user-interface-design-computer-icons-default-stephen-salazar-graphy-user-interface-design-computer-wallpaper-sphere-thumbnail.png"
        } ?: "https://w7.pngwing.com/pngs/177/551/png-transparent-user-interface-design-computer-icons-default-stephen-salazar-graphy-user-interface-design-computer-wallpaper-sphere-thumbnail.png"

        Box(
            modifier = modifier.fillMaxSize()
        ) {
            Column(
                modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Location(date = getCurrentDate(), location = currentLocation)

                Spacer(modifier.height(12.dp))

                MenuCard(
                    annualBalance = annualBalance,
                    annualUsed = annualUsed,
                    annualLeft = annualLeft,
                    userName = userData.fullName,
                    position = userData.positionName ?: "No Position",
                    greeting = "Hello",
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
                    // Note: TopAttendanceCard components have been removed

                    // Note: SeeAllButton for list_time_off and leave history section are commented out

                    Spacer(modifier.height(12.dp))

                    // Tombol lihat semua absensi
                    SeeAllButton(
                        label = stringResource(R.string.attendance_history),
                        onClickButton = navigateListMyAttendance
                    )

                    Spacer(modifier.height(12.dp))

                    // Data Absensi - now using LazyColumn like in DetailsMyAttendance
                    when (attendanceState) {
                        is UiState.Success -> {
                            if (attendanceState.data.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        EmptyListAnimation(modifier = Modifier.size(150.dp))
                                        Text(
                                            text = "No attendance records found",
                                            style = headline4,
                                        )
                                    }
                                }
                            } else {
                                // Using a LazyColumn with a fixed height constraint for the top 5 records
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(320.dp), // Fixed height to prevent constraint issues
                                    contentPadding = PaddingValues(bottom = 8.dp)
                                ) {
                                    items(attendanceState.data.take(5)) { attendance ->
                                        AttendanceHistoryC(record = attendance)
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }

                        is UiState.Error -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    EmptyListAnimation(modifier = Modifier.size(150.dp))
                                    Text(
                                        text = attendanceState.errorMessage,
                                        style = headline4,
                                    )
                                }
                            }
                        }

                        is UiState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                LoadingAnimation()
                            }
                        }

                        is UiState.Idle -> {
                            // Do nothing or show a placeholder if needed
                        }
                    }
                }
            }
        }
    }
}