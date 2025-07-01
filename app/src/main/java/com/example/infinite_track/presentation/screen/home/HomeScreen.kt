package com.example.infinite_track.presentation.screen.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.infinite_track.presentation.components.loading.LoadingAnimation
import com.example.infinite_track.presentation.screen.home.content.EmployeeAndManagerComponent
import com.example.infinite_track.presentation.screen.home.content.InternshipContent
import com.example.infinite_track.utils.UiState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    navigateAttendance: () -> Unit = {},
    navigateTimeOffRequest: () -> Unit = {},
    navigateListMyAttendance: () -> Unit = {},
) {
    // Collect all state from centralized HomeViewModel
    val userProfile by viewModel.userProfileState.collectAsState()
    val attendanceState by viewModel.topAttendanceHistoryState.collectAsState()
    val annualBalance by viewModel.annualBalance.collectAsState()
    val annualUsed by viewModel.annualUsed.collectAsState()
    val currentLocation by viewModel.currentAddressState.collectAsState()
    val internshipSummary by viewModel.internshipSummaryState.collectAsState()

    val isLoading = attendanceState is UiState.Loading
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        content = {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (userProfile == null) {
                    // Show loading indicator when user profile is not yet loaded
                    LoadingAnimation()
                } else {
                    // Display content based on user role
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        when (userProfile?.roleName) {
                            "Internship" -> {
                                // Use the InternshipContent with live data from API
                                InternshipContent(
                                    user = userProfile,
                                    summaryData = internshipSummary, // Pass the nullable summary directly
                                    currentLocation = currentLocation,
                                    attendanceState = attendanceState,
                                    navigateToListMyAttendance = navigateListMyAttendance
                                )
                            }

                            "Admin", "Employee", "Management" -> {
                                // Pass all required state to the "dumb" component
                                EmployeeAndManagerComponent(
                                    user = userProfile,
                                    attendanceState = attendanceState,
                                    annualBalance = annualBalance,
                                    annualUsed = annualUsed,
                                    currentLocation = currentLocation,
                                    isLoading = isLoading,
                                    navigateAttendance = navigateAttendance,
                                    navigateTimeOffRequest = navigateTimeOffRequest,
//                                    navigateListTimeOff = navigateListTimeOff,
                                    navigateListMyAttendance = navigateListMyAttendance
                                )
                            }

                            else -> {
                                // Display a message for unknown role
                                Text("Unknown user role: ${userProfile?.roleName}")
                            }
                        }
                    }
                }
            }
        }
    )
}
