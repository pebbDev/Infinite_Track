package com.example.infinite_track.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.infinite_track.presentation.components.navigation.BottomBarInternship
import com.example.infinite_track.presentation.components.navigation.BottomBarStaff
import com.example.infinite_track.presentation.components.user.home.EmployeeAndManagerComponent
import com.example.infinite_track.presentation.components.user.home.InternshipContent

@Composable
fun MainNavGraph(
    user: String,
    navigateAttendance: () -> Unit,
    navigateTimeOffRequest: () -> Unit,
    navigateListTimeOff: () -> Unit,
    navigateListMyAttendance: () -> Unit,
) {
    when (user) {
        "Employee", "Management" -> EmployeeAndManagerComponent(
            navigateAttendance = navigateAttendance,
            navigateTimeOffRequest = navigateTimeOffRequest,
            navigateListTimeOff = navigateListTimeOff,
            navigateListMyAttendance = navigateListMyAttendance
        )

        "Internship" -> InternshipContent(
            navigateToListMyAttendance = navigateListMyAttendance
        )
    }
}

@Composable
fun BottomBarCore(user: String, navController: NavController) {
    when (user) {
        "Employee"-> BottomBarStaff(navController)
        "Internship" -> BottomBarInternship(navController)
        "Management" -> BottomBarStaff(navController)
    }
}
