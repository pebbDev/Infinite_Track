package com.example.infinite_track.presentation.navigation

import FAQCategoryList
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.infinite_track.data.soucre.dummy.dummyTimeOff
import com.example.infinite_track.domain.model.profile.faqsList
import com.example.infinite_track.domain.model.userdata.UserModel
import com.example.infinite_track.presentation.screen.attendance.AttendanceScreen
import com.example.infinite_track.presentation.screen.auth.login.LoginScreen
import com.example.infinite_track.presentation.screen.contact.ContactScreen
import com.example.infinite_track.presentation.screen.history.HistoryScreen
import com.example.infinite_track.presentation.screen.home.HomeScreen
import com.example.infinite_track.presentation.screen.home.details.DetailsMyAttendance
import com.example.infinite_track.presentation.screen.home.details.DetailsTimeOffRequest
import com.example.infinite_track.presentation.screen.leave_request.leave.LeaveRequestScreen
import com.example.infinite_track.presentation.screen.leave_request.my_leave.MyLeave
import com.example.infinite_track.presentation.screen.leave_request.timeOff.TimeOffScreen
import com.example.infinite_track.presentation.screen.profile.ProfileScreen
import com.example.infinite_track.presentation.screen.profile.ProfileViewModel
import com.example.infinite_track.presentation.screen.profile.details.contactUs.ContactUsScreen
import com.example.infinite_track.presentation.screen.profile.details.edit_profile.EditProfil
import com.example.infinite_track.presentation.screen.profile.details.language.MultiLanguageViewModel
import com.example.infinite_track.presentation.screen.profile.details.my_document.MyDocumentScreen
import com.example.infinite_track.presentation.screen.profile.details.pay_slip.PaySlipScreen
import com.example.infinite_track.utils.safeNavigate

fun NavGraphBuilder.appNavGraph(
    navController: NavHostController,
    user: UserModel? // User bisa null jika belum login
) {
    composable(Screen.Login.route) {
        LoginScreen(navigateToHome = {
            navController.navigate(Screen.Home.route) {
                // Membersihkan backstack setelah login berhasil
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        })
    }

    composable(Screen.Home.route) {
        user?.let {
            HomeScreen(
                user = it,
                navigateAttendance = { navController.safeNavigate(Screen.Attendance.route) },
                navigateTimeOffRequest = { navController.safeNavigate(Screen.TimeOffRequest.route) },
                navigateListTimeOff = { navController.safeNavigate(Screen.DetailListTimeOff.route) },
                navigateListMyAttendance = { navController.safeNavigate(Screen.DetailMyAttendance.route) }
            )
        }
    }

    composable(Screen.Contact.route) { ContactScreen() }
    composable(Screen.History.route) { HistoryScreen() }
    composable(Screen.MyLeave.route) { MyLeave() }

    composable(Screen.MyDocument.route) {
        MyDocumentScreen(onBackClick = { navController.popBackStack() })
    }

    composable(Screen.PaySlip.route) {
        PaySlipScreen(onBackClick = { navController.popBackStack() })
    }

    composable(Screen.Profile.route) {
        // Fix: Using MultiLanguageViewModel instead of ProfileViewModel
        val multiLanguageViewModel: MultiLanguageViewModel = hiltViewModel()
        ProfileScreen(
            viewModel = multiLanguageViewModel,
            navigateToEditProfile = { navController.safeNavigate(Screen.EditProfile.route) },
            navigateToContactUs = { navController.safeNavigate(Screen.ContactUs.route) },
            navigateToFAQ = { navController.safeNavigate(Screen.FAQ.route) },
            navigateToMyDocument = { navController.safeNavigate(Screen.MyDocument.route) },
            navigateToPaySlip = { navController.safeNavigate(Screen.PaySlip.route) },
            navHostController = navController
        )
    }

    composable(Screen.FAQ.route) {
        FAQCategoryList(
            onBackClick = { navController.popBackStack() },
            categories = faqsList
        )
    }

    composable(Screen.ContactUs.route) {
        ContactUsScreen(onBackClick = { navController.popBackStack() })
    }

    composable(Screen.Attendance.route) {
        AttendanceScreen(onBackClick = { navController.popBackStack() })
    }

    composable(Screen.DetailListTimeOff.route) {
        DetailsTimeOffRequest(onBackClick = { navController.popBackStack() })
    }

    composable(Screen.TimeOffRequest.route) {
        user?.let { currentUser ->
            LeaveRequestScreen(
                onBackClick = { navController.popBackStack() },
                user = currentUser
            )
        }
    }

    composable(Screen.DetailMyAttendance.route) {
        DetailsMyAttendance(onBackClick = { navController.popBackStack() })
    }

    composable(Screen.TimeOffReq.route) {
        TimeOffScreen(onBackClick = { navController.popBackStack() }, cards = dummyTimeOff)
    }

    composable(Screen.EditProfile.route) {
        user?.let { currentUser ->
            val profileViewModel: ProfileViewModel = hiltViewModel()
            EditProfil(
                onBackClick = { navController.popBackStack() },
                onCancelClick = { navController.popBackStack() },
                onEditClick = {
                    profileViewModel.updateProfile(
                        userId = currentUser.userId ?: 0,
                        phone_number = currentUser.phone_number,
                        nip_nim = currentUser.nip_nim,
                        address = currentUser.address,
                        start_contract = currentUser.start_contract,
                        end_contract = currentUser.end_contract
                    )
                },
                user = currentUser
            )
        }
    }
}
