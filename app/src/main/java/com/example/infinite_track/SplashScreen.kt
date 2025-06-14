package com.example.infinite_track

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.infinite_track.presentation.components.base.BaseLayout
import com.example.infinite_track.presentation.navigation.Screen
import com.example.infinite_track.presentation.screen.auth.AuthViewModel

@Composable
fun SplashScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val user by authViewModel.getUser().observeAsState()

    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.splash_animation))
    val progress by animateLottieCompositionAsState(composition)

    LaunchedEffect(progress) {
        if (progress == 1f) {
            if (user?.token != null) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                }
            } else {
                navController.navigate(Screen.Login.route)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        BaseLayout()
        LottieAnimation(composition = composition, modifier = Modifier.fillMaxSize())
    }
}