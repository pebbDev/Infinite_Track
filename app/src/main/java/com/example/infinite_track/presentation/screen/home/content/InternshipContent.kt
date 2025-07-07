package com.example.infinite_track.presentation.screen.home.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.infinite_track.R
import com.example.infinite_track.domain.model.attendance.AttendanceRecord
import com.example.infinite_track.domain.model.auth.UserModel
import com.example.infinite_track.domain.model.booking.BookingHistoryItem
import com.example.infinite_track.domain.model.dashboard.InternshipSummary
import com.example.infinite_track.presentation.components.button.SeeAllButton
import com.example.infinite_track.presentation.components.cards.AttendanceHistoryC
import com.example.infinite_track.presentation.components.cards.BookingHistoryCard
import com.example.infinite_track.presentation.components.cards.CardAbsence
import com.example.infinite_track.presentation.components.empty.EmptyListAnimation
import com.example.infinite_track.presentation.components.images.ImageSlider
import com.example.infinite_track.presentation.components.loading.LoadingAnimation
import com.example.infinite_track.presentation.components.tittle.Location
import com.example.infinite_track.presentation.components.tittle.nameCards
import com.example.infinite_track.presentation.core.headline4
import com.example.infinite_track.utils.UiState
import com.example.infinite_track.utils.getCurrentDate

@Composable
fun InternshipContent(
    modifier: Modifier = Modifier,
    user: UserModel?,
    summaryData: InternshipSummary?,
    currentLocation: String,
    attendanceState: UiState<List<AttendanceRecord>>,
    bookingHistoryState: UiState<List<BookingHistoryItem>>,
    navigateToListMyAttendance: () -> Unit,
    navigateToBookingHistory: () -> Unit,
) {
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

            user?.let { userData ->
                nameCards(
                    greeting = "Hello",
                    userName = userData.fullName,
                    division = userData.positionName ?: "Position",
                    profileImage = userData.photoUrl?.ifEmpty {
                        "https://w7.pngwing.com/pngs/177/551/png-transparent-user-interface-design-computer-icons-default-stephen-salazar-graphy-user-interface-design-computer-wallpaper-sphere-thumbnail.png"
                    }
                        ?: "https://w7.pngwing.com/pngs/177/551/png-transparent-user-interface-design-computer-icons-default-stephen-salazar-graphy-user-interface-design-computer-wallpaper-sphere-thumbnail.png"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (summaryData != null) {
                // Define card configurations for the grid
                val cardConfigurations = listOf(
                    Triple(
                        "Checked In",
                        summaryData.checkedInTime ?: "--:--",
                        R.drawable.ic_checkin
                    ),
                    Triple(
                        "Checked Out",
                        summaryData.checkedOutTime ?: "--:--",
                        R.drawable.ic_checkout
                    ),
                    Triple("Absence", "${summaryData.totalAbsence} Day", R.drawable.ic_absence),
                    Triple(
                        "Total Attended",
                        "${summaryData.totalAttended} Day",
                        R.drawable.ic_total_absence
                    )
                )

                // Create a 2x2 grid of summary cards
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = false
                ) {
                    items(cardConfigurations) { (cardText, cardTitle, cardImage) ->
                        CardAbsence(
                            cardTitle = cardTitle,
                            cardText = cardText,
                            cardImage = cardImage,
                            onClick = { /* Handle card click if needed */ }
                        )
                    }
                }
            } else {
                // Add a spacer when data is not available
                Spacer(modifier = Modifier.height(180.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))
            ImageSlider()
            Spacer(modifier = Modifier.height(12.dp))

            // First: Attendance History Section
            SeeAllButton(
                label = stringResource(R.string.attendance_history),
                onClickButton = navigateToListMyAttendance
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Display attendance history
            when (attendanceState) {
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
                        // Using Column instead of LazyColumn for better height management
                        val attendanceItems = attendanceState.data.take(3) // Max 3 items
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            attendanceItems.forEach { attendance ->
                                AttendanceHistoryC(record = attendance)
                            }
                        }
                    }
                }

                is UiState.Error -> {
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
                                text = attendanceState.errorMessage,
                                style = headline4,
                            )
                        }
                    }
                }

                is UiState.Idle -> {
                    // Do nothing or show a placeholder if needed
                }
            }

            // Dynamic spacing - only add spacer if there are attendance items
            if (attendanceState is UiState.Success && attendanceState.data.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Second: Booking History Section
            SeeAllButton(
                label = "Riwayat Booking WFA",
                onClickButton = navigateToBookingHistory
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Display booking history
            when (bookingHistoryState) {
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

                is UiState.Success -> {
                    if (bookingHistoryState.data.isEmpty()) {
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
                                    text = "No booking records found",
                                    style = headline4,
                                )
                            }
                        }
                    } else {
                        // Using Column instead of LazyColumn for better height management
                        val bookingItems = bookingHistoryState.data.take(3) // Max 3 items
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            bookingItems.forEach { booking ->
                                BookingHistoryCard(booking = booking)
                            }
                        }
                    }
                }

                is UiState.Error -> {
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
                                text = bookingHistoryState.errorMessage,
                                style = headline4,
                            )
                        }
                    }
                }

                is UiState.Idle -> {
                    // Do nothing or show a placeholder if needed
                }
            }
        }
    }
}
