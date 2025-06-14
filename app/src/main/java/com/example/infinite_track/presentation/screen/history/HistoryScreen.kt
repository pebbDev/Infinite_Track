package com.example.infinite_track.presentation.screen.history

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.infinite_track.R
import com.example.infinite_track.data.soucre.network.response.AttendanceHistoryResponseItem
import com.example.infinite_track.domain.model.AhistoryModel
import com.example.infinite_track.presentation.components.base.StaticBaseLayout
import com.example.infinite_track.presentation.components.cards.AttendanceHistoryC
import com.example.infinite_track.presentation.components.cards.OverviewCardAttendance
import com.example.infinite_track.presentation.components.empty.EmptyListAnimation
import com.example.infinite_track.presentation.components.loading.LoadingAnimation
import com.example.infinite_track.presentation.components.tittle.Tittle
import com.example.infinite_track.presentation.screen.home.content.EmployeeViewModel
import com.example.infinite_track.presentation.core.headline3
import com.example.infinite_track.presentation.core.headline4
import com.example.infinite_track.presentation.theme.Infinite_TrackTheme
import com.example.infinite_track.utils.UiState
import com.example.infinite_track.utils.calculateTotalCourse
import com.example.infinite_track.utils.isInternetAvailable

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
    overviewViewModel: AttendanceOverviewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val attendanceState by employeeViewModel.attendanceHistoryRepositoryState.collectAsState()
    val attendanceOverviewData by overviewViewModel.overviewData.collectAsState()

    LaunchedEffect(Unit) {
        if (isInternetAvailable(context)) {
            employeeViewModel.getAllAttendance()
            employeeViewModel.getOverview()
        } else {
            employeeViewModel.setError("Tidak ada koneksi internet")
        }
    }

    LaunchedEffect(Unit) {
        if (isInternetAvailable(context)) {
            overviewViewModel.fetchAttendanceOverview()
        } else {
            employeeViewModel.setError("Tidak ada koneksi internet")
        }
    }

    Scaffold(
        modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.history_label),
                    style = headline3,
                )
            }
        }
    ) { innerPadding ->

        StaticBaseLayout()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Tittle(tittle = stringResource(R.string.attendance_overview_title))
                    Spacer(modifier = Modifier.height(24.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(attendanceOverviewData) { data ->
                            OverviewCardAttendance(
                                title = data.title,
                                count = data.count,
                                unit = data.unit,
                                onClick = {}
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(Color.White)
                        .border(
                            width = 1.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        ),
                    Alignment.BottomCenter
                ) {
                    when (attendanceState) {
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
                        }

                        is UiState.Success -> {
                            val attendanceList =
                                (attendanceState as UiState.Success<List<AttendanceHistoryResponseItem>>).data
                                    .sortedByDescending {
                                        it.attendanceId ?: 0
                                    }
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 24.dp, start = 20.dp, end = 20.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 50.dp)
                            ) {
                                item {
                                    Tittle(tittle = stringResource(R.string.attendance_summary_title))
                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                items(attendanceList) { summary ->
                                    AttendanceHistoryC(
                                        ahistory = AhistoryModel(
                                            id = summary.attendanceId ?: 0,
                                            date = summary.attendanceDate ?: "N/A",
                                            monthYear = summary.attendanceMonthYear ?: "N/A",
                                            checkIn = summary.checkInTime ?: "N/A",
                                            checkOut = summary.checkOutTime ?: "--:--",
                                            totalCourse = calculateTotalCourse(
                                                summary.checkInTime,
                                                summary.checkOutTime
                                            )
                                        )
                                    )
                                }
                            }
                        }

                        is UiState.Error -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .offset(y = 100.dp)
                                ) {
                                    EmptyListAnimation(modifier = Modifier.size(150.dp))
                                    Text(
                                        text = (attendanceState as UiState.Error).errorMessage,
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
}


@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    Infinite_TrackTheme {
        HistoryScreen()
    }
}
