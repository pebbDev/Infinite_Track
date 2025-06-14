package com.example.infinite_track.presentation.screen.profile

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import cn.pedant.SweetAlert.SweetAlertDialog
import coil.compose.AsyncImage
import com.example.infinite_track.R
import com.example.infinite_track.presentation.components.base.BaseLayout
import com.example.infinite_track.presentation.components.base.StaticBaseLayout
import com.example.infinite_track.presentation.components.popUp.LanguagePopUp
import com.example.infinite_track.presentation.core.headline2
import com.example.infinite_track.presentation.core.headline3
import com.example.infinite_track.presentation.core.headline4
import com.example.infinite_track.presentation.navigation.Screen
import com.example.infinite_track.presentation.screen.auth.AuthViewModel
import com.example.infinite_track.presentation.screen.profile.details.language.MultiLanguageViewModel
import com.example.infinite_track.presentation.theme.Purple_300
import com.example.infinite_track.presentation.theme.Purple_500
import com.example.infinite_track.utils.AppLanguageUpdater
import com.example.infinite_track.utils.DialogHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    viewModel: MultiLanguageViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    navigateToEditProfile: () -> Unit,
    navigateToContactUs: () -> Unit,
    navigateToFAQ: () -> Unit,
    navigateToMyDocument: () -> Unit,
    navigateToPaySlip: () -> Unit,
    navHostController: NavHostController
) {
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val user by authViewModel.getUser().observeAsState()
    var dialog by remember { mutableStateOf<SweetAlertDialog?>(null) }

    val context = LocalContext.current

    val baseUrl = "http://10.0.2.2:3000/"

    val fullImageUrl = if (user?.profilePhoto?.isNotEmpty() == true) {
        user?.profilePhoto
    }
    else {
        "https://w7.pngwing.com/pngs/177/551/png-transparent-user-interface-design-computer-icons-default-stephen-salazar-graphy-user-interface-design-computer-wallpaper-sphere-thumbnail.png"
    }

    AppLanguageUpdater(language = selectedLanguage)

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ){
        StaticBaseLayout()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column (
                        modifier = Modifier
                            .width(280.dp)
                    ){
                        Text(
                            text = user?.userName ?: "N/A",
                            style = headline2,
                            color = Purple_500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = user?.positionName ?: "N/A",
                            style = headline4,
                            color = Purple_300,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    AsyncImage(
                        model = fullImageUrl,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .height(60.dp)
                            .width(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.account_information),
                        style = headline3,
                        fontWeight = FontWeight.Medium
                    )
                    ProfileBar(
                        label = stringResource(R.string.edit_profile),
                        onClick = navigateToEditProfile,
                        icon = R.drawable.ic_pencil
                    )
                    ProfileBar(
                        label = stringResource(R.string.pay_slip),
                        onClick = navigateToPaySlip,
                        icon = R.drawable.ic_payslip
                    )
                    ProfileBar(
                        label = stringResource(R.string.my_document),
                        onClick = navigateToMyDocument,
                        icon = R.drawable.ic_mydocument
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Settings Section
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.setting),
                        style = headline3,
                        fontWeight = FontWeight.Medium
                    )

                    // Language Selector Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clickable { showDialog = true },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_global),
                            contentDescription = stringResource(R.string.language),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.language),
                            style = headline4,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = if (selectedLanguage == "en") "English" else "Indonesia",
                            style = headline4,
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            painter = painterResource(R.drawable.right_arrow),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Language Pop-Up
                    LanguagePopUp(
                        showDialog = showDialog,
                        selectedLanguage = selectedLanguage,
                        onDismiss = { showDialog = false },
                        onLanguageChange = { viewModel.updateLanguage(it) },
                        onConfirm = { showDialog = false }
                    )

                    // Other Settings Options
                    ProfileBar(
                        label = stringResource(R.string.contact_us),
                        onClick = navigateToContactUs,
                        icon = R.drawable.ic_contactus
                    )
                    ProfileBar(
                        label = stringResource(R.string.faq),
                        onClick = navigateToFAQ,
                        icon = R.drawable.ic_faq
                    )
                    ProfileBar(
                        label = stringResource(R.string.about_us),
                        onClick = {},
                        icon = R.drawable.ic_community
                    )
                    ProfileBar(
                        label = stringResource(R.string.logOut),
                        icon = R.drawable.ic_logout,
                        onClick = {
                            dialog = DialogHelper.showDialogWarning(
                                context = context,
                                title = "Log out",
                                textContent = "Are you sure you want to log out?",

                                onDismis = { dialog?.dismissWithAnimation() },
                                onConfirm = {
                                    dialog?.dismissWithAnimation()
                                    dialog = DialogHelper.showDialogLoading(
                                        context = context,
                                        textContent = "Please wait"
                                    )
                                    scope.launch {
                                        delay(2000)
                                        dialog?.dismissWithAnimation()
                                        authViewModel.logout()
                                        navHostController.navigate(Screen.Login.route) {
                                            popUpTo(navHostController.graph.findStartDestination().id) {
                                            }
                                            restoreState = true
                                            launchSingleTop = true
                                        }
                                        Toast
                                            .makeText(
                                                context,
                                                "Log out success",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                }
                            )
                        },

                        )
                }
            }
        }
    }
}

@Composable
fun ProfileBar(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = headline4,
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(R.drawable.right_arrow),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
    }
}