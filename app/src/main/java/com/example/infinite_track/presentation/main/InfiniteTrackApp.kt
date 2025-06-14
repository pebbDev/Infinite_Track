package com.example.infinite_track.presentation.main

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.infinite_track.presentation.components.base.BaseLayout
import com.example.infinite_track.presentation.components.button.customfab.CustomFAB
import com.example.infinite_track.presentation.components.navigation.BottomBarInternship
import com.example.infinite_track.presentation.components.navigation.BottomBarStaff
import com.example.infinite_track.presentation.navigation.Screen
import com.example.infinite_track.presentation.navigation.appNavGraph
import com.example.infinite_track.presentation.screen.auth.AuthViewModel
import com.example.infinite_track.utils.safeNavigate

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun InfiniteTrackApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    val user by authViewModel.getUser().observeAsState()
    val userRole = user?.userRole ?: ""

    // State animation defined at the app level - this will persist across screen navigations
    val infiniteTransition = rememberInfiniteTransition(label = "infinite_rotation")
    val rotationAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val sizeTransition = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        sizeTransition.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 10000
                    0f at 0
                    1f at 5000
                    0f at 10000
                },
                repeatMode = RepeatMode.Restart
            )
        )
    }

    val screensWithoutBottomBar = listOf(
        Screen.Login.route,
        Screen.Splash.route,
        Screen.Attendance.route,
        Screen.EditProfile.route,
        Screen.DetailMyAttendance.route,
        Screen.DetailListTimeOff.route,
        Screen.TimeOffRequest.route,
        Screen.FAQ.route
    )

    val isBottomBarVisible = currentRoute !in screensWithoutBottomBar

    // Main box that contains both the BaseLayout and the rest of the UI
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.Transparent // Make the entire app surface transparent
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // BaseLayout ditampilkan di layer paling belakang
            BaseLayout(
                rotation = rotationAnimation,
                size = sizeTransition.value,
                modifier = Modifier.zIndex(-2f) // Diubah ke -2f agar berada di belakang layer putih transparan
            )

            // Layer putih semi-transparan di atas BaseLayout
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(-1f) // Diubah ke -1f agar berada di atas BaseLayout
                    .background(Color.White.copy(alpha = 0.5f))
            )


            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent, // Makes scaffold background transparent so BaseLayout is visible
                contentColor = MaterialTheme.colorScheme.onBackground, // Ensures text is visible
                bottomBar = {
                    if (isBottomBarVisible) {
                        when (userRole) {
                            "Internship" -> BottomBarInternship(navController = navController)
                            "Admin", "Employee", "Management" -> BottomBarStaff(navController = navController)
                            else -> {}
                        }
                    }
                },
                floatingActionButton = {
                    if (isBottomBarVisible && (userRole == "Management" || userRole == "Internship")) {
                        CustomFAB(userRole = userRole) {
                            when (userRole) {
                                "Internship" -> navController.safeNavigate(Screen.Attendance.route)
                                "Management" -> navController.safeNavigate(Screen.TimeOffReq.route)
                                else -> Toast.makeText(
                                    context,
                                    "Role not recognized",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                },
                floatingActionButtonPosition = FabPosition.Center,
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (currentRoute !in screensWithoutBottomBar) Modifier.padding(
                                innerPadding
                            )
                            else Modifier.systemBarsPadding()
                        )
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = if (user?.token != "null" && user?.token != null) Screen.Home.route else Screen.Login.route,
                    ) {
                        appNavGraph(navController, user)
                    }
                }
            }
        }
    }
}
