package com.example.infinite_track.presentation.screen.history

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.infinite_track.R
import com.example.infinite_track.data.soucre.network.response.AttendanceHistoryResponseItem
import com.example.infinite_track.domain.model.AhistoryModel
import com.example.infinite_track.presentation.components.cards.AttendanceHistoryC
import com.example.infinite_track.presentation.components.cards.OverviewCardAttendance
import com.example.infinite_track.presentation.components.empty.EmptyListAnimation
import com.example.infinite_track.presentation.components.loading.LoadingAnimation
import com.example.infinite_track.presentation.components.tittle.Tittle
import com.example.infinite_track.presentation.core.headline3
import com.example.infinite_track.presentation.core.headline4
import com.example.infinite_track.presentation.screen.home.content.EmployeeViewModel
import com.example.infinite_track.utils.UiState
import com.example.infinite_track.utils.calculateTotalCourse
import com.example.infinite_track.utils.isInternetAvailable

@OptIn(ExperimentalMaterial3Api::class)
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

    val scaffoldState = rememberBottomSheetScaffoldState()

    LaunchedEffect(Unit) {
        if (isInternetAvailable(context)) {
            employeeViewModel.getAllAttendance()
            overviewViewModel.fetchAttendanceOverview()
        } else {
            employeeViewModel.setError("Tidak ada koneksi internet")
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 200.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = Color.White.copy(alpha = 0.5f),
        containerColor = Color.Transparent,
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.history_label),
                    style = headline3,
                )
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Tittle(tittle = stringResource(R.string.attendance_overview_title))
                Spacer(modifier = Modifier.height(24.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
        },
        // KONTEN DI DALAM BOTTOM SHEET
        sheetContent = {
            // Konten LazyColumn untuk riwayat absensi
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 500.dp) // Pastikan box konten sheet lebih tinggi dari peekHeight
            ) {
                when (attendanceState) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingAnimation()
                        }
                    }

                    is UiState.Success -> {
                        val attendanceList =
                            (attendanceState as UiState.Success<List<AttendanceHistoryResponseItem>>).data
                                .sortedByDescending { it.attendanceId ?: 0 }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
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
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                    is UiState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            EmptyListAnimation(modifier = Modifier.size(150.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = (attendanceState as UiState.Error).errorMessage,
                                style = headline4,
                            )
                        }
                    }

                    is UiState.Idle -> {
                    }
                }
            }
        }
    )
}
