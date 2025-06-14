package com.example.infinite_track.presentation.main

import FAQCategoryList
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.infinite_track.data.soucre.dummy.dummyTimeOff
import com.example.infinite_track.domain.model.profile.faqsList
import com.example.infinite_track.presentation.components.base.StaticBaseLayout
import com.example.infinite_track.presentation.components.button.customfab.CustomFAB
import com.example.infinite_track.presentation.components.loading.LoadingAnimation
import com.example.infinite_track.presentation.navigation.BottomBarCore
import com.example.infinite_track.presentation.navigation.Screen
import com.example.infinite_track.presentation.screen.attendance.AttendanceScreen
import com.example.infinite_track.presentation.screen.auth.AuthViewModel
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
import com.example.infinite_track.presentation.screen.profile.details.my_document.MyDocumentScreen
import com.example.infinite_track.presentation.screen.profile.details.pay_slip.PaySlipScreen
import com.example.infinite_track.utils.safeNavigate

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun InfiniteTrackApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: ""

    val user by authViewModel.getUser().observeAsState()

    val hideBottomBarRoutes = listOf(
        Screen.Attendance.route,
        Screen.DetailMyAttendance.route,
        Screen.EditProfile.route,
        Screen.DetailListTimeOff.route,
        Screen.TimeOffRequest.route,
        Screen.FAQ.route
    )

    val hideInnerPaddingRoutes = listOf(
        Screen.Attendance.route,
        Screen.EditProfile.route,
        Screen.DetailMyAttendance.route,
        Screen.DetailListTimeOff.route,
        Screen.History.route,
        Screen.TimeOffRequest.route
    )

    Scaffold(
        modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        bottomBar = {
            if (currentRoute !in hideBottomBarRoutes) {
                BottomBarCore(user = user?.userRole ?: "", navController = navController)
            }
        },
        floatingActionButton = {
            if (currentRoute !in hideBottomBarRoutes) {
                CustomFAB(currentRoute = currentRoute) {
                    when (user?.userRole) {
                        "Internship" -> navController.safeNavigate(Screen.Attendance.route)
                        "Management" -> navController.safeNavigate(Screen.TimeOffReq.route)
                        else -> Toast.makeText(context, "Role not recognized", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        StaticBaseLayout()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (currentRoute !in hideInnerPaddingRoutes) Modifier.padding(innerPadding)
                    else Modifier.systemBarsPadding()
                )
        ) {
            NavHost(
                navController = navController,
                startDestination = if (user?.token != "null") Screen.Home.route else Screen.Login.route,
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(navigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    })
                }
                composable(Screen.Home.route) {
                    user?.let {
                        HomeScreen(
                            user = it,
                            navigateAttendance = {
                                navController.safeNavigate(Screen.Attendance.route)
                            },
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
                    MyDocumentScreen(
                        onBackClick = { navController.safeNavigate(Screen.Profile.route) }
                    )
                }
                composable(Screen.PaySlip.route) {
                    PaySlipScreen(
                        onBackClick = { navController.safeNavigate(Screen.Profile.route) }
                    )
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(
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
                        onBackClick = { navController.safeNavigate(Screen.Profile.route) },
                        categories = faqsList
                    )
                }
                composable(Screen.ContactUs.route) {
                    ContactUsScreen(onBackClick = {
                        navController.safeNavigate(Screen.Profile.route)
                    })
                }
                composable(Screen.Attendance.route) {
                    AttendanceScreen(
                        onBackClick = { navController.safeNavigate(Screen.Home.route) }
                    )
                }
                composable(Screen.DetailListTimeOff.route) {
                    DetailsTimeOffRequest(
                        onBackClick = { navController.safeNavigate(Screen.Home.route) }
                    )
                }
                composable(Screen.TimeOffRequest.route) {
                    user?.let { currentUser ->
                        LeaveRequestScreen(
                            onBackClick = { navController.safeNavigate(Screen.Home.route) },
                            user = currentUser
                        )
                    }
                }
                composable(Screen.DetailListTimeOff.route) {
                    DetailsTimeOffRequest(
                        onBackClick = { navController.safeNavigate(Screen.Home.route) }
                    )
                }
                composable(Screen.DetailMyAttendance.route) {
                    DetailsMyAttendance(
                        onBackClick = { navController.safeNavigate(Screen.Home.route) }
                    )
                }
                composable(Screen.TimeOffReq.route) { TimeOffScreen(cards = dummyTimeOff) }
                composable(Screen.EditProfile.route) {
                    user?.let { currentUser ->
                        EditProfil(
                            onBackClick = { navController.safeNavigate(Screen.Profile.route) },
                            onCancelClick = { navController.navigateUp() },
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
                    } ?: run {
                        LoadingAnimation()
                    }
                }
            }
        }
    }
}
