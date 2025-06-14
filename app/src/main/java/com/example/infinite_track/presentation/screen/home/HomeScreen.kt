package com.example.infinite_track.presentation.screen.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.infinite_track.domain.model.userdata.UserModel
import com.example.infinite_track.presentation.components.base.StaticBaseLayout
import com.example.infinite_track.presentation.screen.home.content.EmployeeAndManagerComponent
import com.example.infinite_track.presentation.screen.home.content.InternshipContent

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    user: UserModel,
    navigateAttendance: () -> Unit,
    navigateTimeOffRequest: () -> Unit,
    navigateListTimeOff: () -> Unit,
    navigateListMyAttendance: () -> Unit,

    ) {
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        content = {
            StaticBaseLayout()
            Box(
                modifier = modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    when (user.userRole) {
                        "Internship" -> {
                            InternshipContent(
                                navigateToListMyAttendance = navigateListMyAttendance
                            )
                        }
                        "Admin", "Employee", "Management" -> {
                            EmployeeAndManagerComponent(
                                navigateAttendance = navigateAttendance,
                                navigateTimeOffRequest = navigateTimeOffRequest,
                                navigateListTimeOff = navigateListTimeOff,
                                navigateListMyAttendance = navigateListMyAttendance
                            )
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    )
}