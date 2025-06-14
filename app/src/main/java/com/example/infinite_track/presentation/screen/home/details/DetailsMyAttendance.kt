package com.example.infinite_track.presentation.screen.home.details

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.infinite_track.data.soucre.network.response.AttendanceHistoryResponseItem
import com.example.infinite_track.domain.model.AhistoryModel
import com.example.infinite_track.presentation.components.base.StaticBaseLayout
import com.example.infinite_track.presentation.components.button.InfiniteTracButtonBack
import com.example.infinite_track.presentation.components.calendar.DateRangePickerModal
import com.example.infinite_track.presentation.components.calendar.TodayDateWithFilterHeader
import com.example.infinite_track.presentation.components.cards.AttendanceHistoryC
import com.example.infinite_track.presentation.components.empty.EmptyListAnimation
import com.example.infinite_track.presentation.components.loading.LoadingAnimation
import com.example.infinite_track.presentation.components.user.home.EmployeeViewModel
import com.example.infinite_track.presentation.core.headline4
import com.example.infinite_track.utils.UiState
import com.example.infinite_track.utils.calculateTotalCourse
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailsMyAttendance(
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val historyState by employeeViewModel.attendanceHistoryRepositoryState.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var filteredHistory by remember { mutableStateOf<List<AttendanceHistoryResponseItem>>(emptyList()) }
    var selectedDateRange by remember { mutableStateOf<String?>(null) }

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        employeeViewModel.getAllAttendance()
        visible = true
    }

    fun parseAttendanceDate(dateString: String?, monthYear: String?): LocalDate? {
        return try {
            if (dateString != null && monthYear != null) {
                val formattedDate = "$dateString $monthYear"
                val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)
                LocalDate.parse(formattedDate, formatter)
            } else {
                Log.e("ParseAttendanceDate", "Date or MonthYear is null")
                null
            }
        } catch (e: Exception) {
            Log.e("ParseAttendanceDate", "Error parsing date: $dateString $monthYear", e)
            null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            InfiniteTracButtonBack(
                title = "My Attendance",
                navigationBack = onBackClick,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    ) { innerPadding ->
        StaticBaseLayout()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Header with date filter
                TodayDateWithFilterHeader(
                    displayDate = selectedDateRange ?: "All Attendance Data",
                    onFilterClick = { expanded = true }
                )

                // Date range picker modal
                if (expanded) {
                    DateRangePickerModal(
                        onDateRangeSelected = { dateRange ->
                            val (startMillis, endMillis) = dateRange
                            if (startMillis != null && endMillis != null) {
                                val startDate = Instant.ofEpochMilli(startMillis)
                                    .atZone(ZoneId.systemDefault()).toLocalDate()

                                val endDate = Instant.ofEpochMilli(endMillis)
                                    .atZone(ZoneId.systemDefault()).toLocalDate()

                                selectedDateRange =
                                    "${
                                        startDate.format(
                                            DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id", "ID"))
                                        )
                                    } - ${
                                        endDate.format(
                                            DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id", "ID"))
                                        )
                                    }"

                                val originalData =
                                    (historyState as? UiState.Success<List<AttendanceHistoryResponseItem>>)?.data
                                        ?: emptyList()

                                filteredHistory = originalData.filter { history ->
                                    val historyDate = parseAttendanceDate(
                                        history.attendanceDate,
                                        history.attendanceMonthYear
                                    )
                                    historyDate != null && !historyDate.isBefore(startDate) && !historyDate.isAfter(
                                        endDate
                                    )
                                }
                                Log.d("FilteredHistory", "Filtered data: $filteredHistory")
                            }
                            expanded = false
                        },
                        onDismiss = { expanded = false }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (historyState) {
                    is UiState.Idle -> {}

                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingAnimation() // Add your custom loading animation
                        }
                    }

                    is UiState.Success -> {
                        val originalData = (historyState as UiState.Success<List<AttendanceHistoryResponseItem>>).data
                        val dataToShow = if (selectedDateRange == null) originalData else filteredHistory

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (dataToShow.isNotEmpty()) {
                                items(dataToShow) { history ->
                                    AnimatedVisibility(
                                        visible = visible,
                                        enter = slideInVertically(
                                            initialOffsetY = { -it },
                                            animationSpec = tween(durationMillis = 300)
                                        ) + fadeIn(animationSpec = tween(durationMillis = 500)),
                                        exit = fadeOut(animationSpec = tween(durationMillis = 300))
                                    ) {
                                        AttendanceHistoryC(
                                            ahistory = AhistoryModel(
                                                id = history.attendanceId ?: 0,
                                                date = history.attendanceDate ?: "N/A",
                                                monthYear = history.attendanceMonthYear ?: "N/A",
                                                checkIn = history.checkInTime ?: "N/A",
                                                checkOut = history.checkOutTime ?: "--:--",
                                                totalCourse = calculateTotalCourse(
                                                    history.checkInTime,
                                                    history.checkOutTime
                                                )
                                            )
                                        )
                                    }
                                }
                            } else {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            EmptyListAnimation(modifier = Modifier.size(150.dp))
                                            Text(
                                                text = "No attendance data found for $selectedDateRange.",
                                                style = headline4,
                                            )
                                        }
                                    }
                                }
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
                            ) {
                                EmptyListAnimation(modifier = Modifier.size(150.dp))
                                Text(
                                    text = (historyState as UiState.Error).errorMessage,
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

