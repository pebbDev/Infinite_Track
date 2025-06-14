package com.example.infinite_track.presentation.screen.home.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.example.infinite_track.domain.model.AhistoryModel
import com.example.infinite_track.presentation.components.button.SeeAllButton
import com.example.infinite_track.presentation.components.cards.AttendanceHistoryC
import com.example.infinite_track.presentation.components.cards.CardAbsence
import com.example.infinite_track.presentation.components.empty.EmptyListAnimation
import com.example.infinite_track.presentation.components.loading.LoadingAnimation
import com.example.infinite_track.presentation.components.tittle.Location
import com.example.infinite_track.presentation.components.tittle.nameCards
import com.example.infinite_track.presentation.core.headline4
import com.example.infinite_track.presentation.screen.auth.AuthViewModel
import com.example.infinite_track.utils.UiState
import com.example.infinite_track.utils.calculateTotalCourse
import com.example.infinite_track.utils.fetchLocation
import com.example.infinite_track.utils.getCurrentDate
import com.example.infinite_track.utils.isInternetAvailable
import kotlinx.coroutines.delay

@Composable
fun InternshipContent(
    modifier: Modifier = Modifier,
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    navigateToListMyAttendance: () -> Unit,
) {
    val context = LocalContext.current
    val user by authViewModel.getUser().observeAsState()
    var currentLocation by remember { mutableStateOf("Loading...") }
    val overviewData = employeeViewModel.overviewData.observeAsState().value


    LaunchedEffect(currentLocation) {
        fetchLocation(context) { location ->
            currentLocation = location
        }
    }

    LaunchedEffect(Unit) {
        if (isInternetAvailable(context)) {
            employeeViewModel.getAllAttendance()
            employeeViewModel.getOverview()
        } else {
            employeeViewModel.setError("Tidak ada koneksi internet")
        }
    }
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 12.dp)
        ) {
            Location(
                date = getCurrentDate(),
                location = currentLocation
            )
            Spacer(modifier = Modifier.height(12.dp))
            nameCards(
                greeting = user?.greeting ?: "Hello",
                userName = user?.userName ?: "User",
                division = user?.positionName ?: "Position",
                profileImage = user?.profilePhoto ?: "https://w7.pngwing.com/pngs/177/551/png-transparent-user-interface-design-computer-icons-default-stephen-salazar-graphy-user-interface-design-computer-wallpaper-sphere-thumbnail.png"
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Row {
                    CardAbsence(
                        cardTitle = overviewData?.overviewData?.checkinTime ?: "-",
                        cardText = "Checked In",
                        cardImage = R.drawable.ic_checkin,
                        onClick = { println("Card 1 clicked!") }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    CardAbsence(
                        cardTitle = overviewData?.overviewData?.checkoutTime ?: "-",
                        cardText = "Checked Out",
                        cardImage = R.drawable.ic_checkout,
                        onClick = { println("Card 3 clicked!") }
                    )
                }
                Row {
                    CardAbsence(
                        cardTitle = "${overviewData?.overviewData?.totalAbsence ?: 0} Day",
                        cardText = "Absence",
                        cardImage = R.drawable.ic_absence,
                        onClick = { println("Card 2 clicked!") }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    CardAbsence(
                        cardTitle = "${overviewData?.overviewData?.totalAttendance ?: 0} Day",
                        cardText = "Total Attended",
                        cardImage = R.drawable.ic_total_absence,
                        onClick = { println("Card 4 clicked!") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
//            ImageSlider()
            Spacer(modifier = Modifier.height(12.dp))

            SeeAllButton(
                label = stringResource(R.string.attendance_history),
                onClickButton = navigateToListMyAttendance
            )
            Spacer(modifier = Modifier.height(8.dp))

            employeeViewModel.attendanceHistoryRepositoryState.collectAsState().value.let { uiState ->
                when (uiState) {
                    is UiState.Idle -> {
                    }

                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingAnimation()
                        }

                        LaunchedEffect(Unit) {
                            delay(2000L)
                            employeeViewModel.getAllAttendance()
                        }
                    }

                    is UiState.Success -> {
                        val topFiveAttendance = uiState.data
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
                                    text = uiState.errorMessage,
                                    style = headline4,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


