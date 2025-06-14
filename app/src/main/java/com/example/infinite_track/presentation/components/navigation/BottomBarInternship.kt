package com.example.infinite_track.presentation.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.infinite_track.R
import com.example.infinite_track.presentation.core.body1
import com.example.infinite_track.presentation.navigation.NavigationItem
import com.example.infinite_track.presentation.navigation.Screen
import com.example.infinite_track.presentation.theme.Blue_500
import com.example.infinite_track.presentation.theme.Infinite_TrackTheme
import com.example.infinite_track.presentation.theme.Purple_500

@Composable
fun BottomBarInternship(navController: NavController) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                shadowElevation = 8.dp.toPx()
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                clip = true
            }
            .background(
                color = Color.White.copy(0.9f),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            ),
        contentColor = Color.Transparent,
        containerColor = Color.White.copy(0.9f),
    ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val navigationItems = listOf(
            NavigationItem(
                tittle = stringResource(R.string.bottom_menu_home),
                selectedIcon = R.drawable.ic_menu_home_selected,
                unselectedIcon = R.drawable.ic_menu_home,
                screen = Screen.Home
            ),
            NavigationItem(
                tittle = stringResource(R.string.bottom_menu_contact),
                selectedIcon = R.drawable.ic_contact_selected,
                unselectedIcon = R.drawable.ic_contact,
                screen = Screen.Contact
            ),
            NavigationItem(
                tittle = stringResource(R.string.bottom_menu_history),
                selectedIcon = R.drawable.ic_history_selected,
                unselectedIcon = R.drawable.ic_history,
                screen = Screen.History
            ),
            NavigationItem(
                tittle = stringResource(R.string.bottom_menu_profile),
                selectedIcon = R.drawable.ic_profile_selected,
                unselectedIcon = R.drawable.ic_profile,
                screen = Screen.Profile
            ),
        )
        navigationItems.map { item ->
            val selected = currentRoute == item.screen.route
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = if (selected) item.selectedIcon else item.unselectedIcon),
                        contentDescription = item.tittle,
                    )
                },
                label = { Text(item.tittle, style = body1) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Blue_500,
                    selectedTextColor = Blue_500,
                    indicatorColor = Color(0x00FFFFFF),
                    unselectedIconColor = Purple_500,
                    unselectedTextColor = Purple_500,
                ),

                selected = selected,
                onClick = {
                    navController.navigate(item.screen.route) {
                        if (item.screen.route == Screen.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }

                            restoreState = true
                            launchSingleTop = true
                        } else {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    }

                }

            )
        }
    }
}


@Composable
@Preview
fun PreviewBottomBarIntership() {
    Infinite_TrackTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Blue)
        ) {
            BottomBarInternship(rememberNavController())
        }
    }
}