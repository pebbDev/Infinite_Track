package com.example.infinite_track.presentation.screen.profile.details.edit_profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.infinite_track.data.soucre.NetworkResponse
import com.example.infinite_track.domain.model.userdata.UserModel
import com.example.infinite_track.presentation.components.avatar.ProfileCard
import com.example.infinite_track.presentation.components.button.CancelButton
import com.example.infinite_track.presentation.components.button.InfiniteTracButtonBack
import com.example.infinite_track.presentation.components.calendar.DatePickerComponentWithLabel
import com.example.infinite_track.presentation.components.profile_textfield.PhoneNumberTextFieldComponent
import com.example.infinite_track.presentation.components.profile_textfield.ProfileTextFieldComponent
import com.example.infinite_track.presentation.components.address.CustomTextArea
import com.example.infinite_track.presentation.components.base.BaseLayout
import com.example.infinite_track.presentation.components.base.StaticBaseLayout
import com.example.infinite_track.presentation.components.button.InfiniteTrackButton
import com.example.infinite_track.presentation.screen.profile.ProfileViewModel
import com.example.infinite_track.utils.formatApiDate

@Composable
fun EditProfil(
    onBackClick: () -> Unit,
    onCancelClick: () -> Unit,
    onEditClick: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    user: UserModel
) {
    var address by remember { mutableStateOf(user.address ?: "") }
    var phoneNumber by remember { mutableStateOf(user.phone_number ?: "") }
    var nipNim by remember { mutableStateOf(user.nip_nim ?: "") }
    var startContract by remember { mutableStateOf(user.start_contract ?: "") }
    var endContract by remember { mutableStateOf(user.end_contract ?: "") }

    var isPhoneError by remember { mutableStateOf(false) }


    val profileState by profileViewModel.profileState.collectAsState()
    var isEditing by remember { mutableStateOf(false) }

    when (profileState) {
        is NetworkResponse.Loading -> CircularProgressIndicator()
        is NetworkResponse.Success -> {
            Toast.makeText(
                LocalContext.current,
                "Profile updated successfully!",
                Toast.LENGTH_SHORT
            ).show()
        }

        is NetworkResponse.Error -> {
            Toast.makeText(
                LocalContext.current,
                (profileState as NetworkResponse.Error).message,
                Toast.LENGTH_SHORT
            ).show()
        }

        else -> {}
    }

    Scaffold(
        topBar = {
            InfiniteTracButtonBack(
                title = "Edit Profile",
                navigationBack = onBackClick,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    ) { innerPadding ->

        StaticBaseLayout()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                ProfileCard(
                    imageResId = com.example.infinite_track.R.drawable.logo,
                    name = user.userName,
                    jobTitle = user.positionName
                )

                Spacer(modifier = Modifier.height(16.dp))

                // TextFields
                ProfileTextFieldComponent(
                    label = "Full Name",
                    value = user.userName,
                    enabled = false
                )

                Spacer(modifier = Modifier.height(8.dp))

                ProfileTextFieldComponent(
                    label = "NIP / NIM",
                    value = nipNim,
                    onValueChange = { nipNim = it },
                    enabled = if ((user.nip_nim == null || user.nip_nim == "") && isEditing == true ) {true
                    } else {false},
                )

                Spacer(modifier = Modifier.height(8.dp))

                ProfileTextFieldComponent(
                    label = "Division",
                    value = user.division ?: "",
                    enabled = false
                )

                Spacer(modifier = Modifier.height(8.dp))

                ProfileTextFieldComponent(
                    label = "Position",
                    value = user.positionName,
                    enabled = false
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DatePickerComponentWithLabel(
                        modifier = Modifier.weight(1f),
                        label = "Start Contract",
                        initialDate = formatApiDate(startContract),
                        onDateSelected = { startContract = it },
                        enabled = if ((user.start_contract == null || user.start_contract == "") && isEditing == true ) {true} else {false}
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    DatePickerComponentWithLabel(
                        modifier = Modifier.weight(1f),
                        label = "End Contract",
                        initialDate =  formatApiDate(endContract),
                        onDateSelected = { endContract = it },
                        enabled = if ((user.end_contract == null || user.end_contract == "") && isEditing == true ) {true} else {false}
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                ProfileTextFieldComponent(
                    label = "Email",
                    value = user.email,
                    enabled = false
                )

                Spacer(modifier = Modifier.height(8.dp))

                PhoneNumberTextFieldComponent(
                    label = "Phone Number",
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        isPhoneError = !android.util.Patterns.PHONE.matcher(it).matches()
                    },
                    enabled = isEditing
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextArea(
                    value = address,
                    enabled = isEditing,
                    onTextChanged = {
                        address = it
                    },
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (isEditing) {
                        InfiniteTrackButton(
                            label = "Save",
                            onClick = {
                                onEditClick()
                                isEditing = false
                                profileViewModel.updateProfile(
                                    userId = user.userId ?: 0,
                                    phone_number = phoneNumber,
                                    nip_nim = nipNim,
                                    address = address,
                                    start_contract = startContract,
                                    end_contract = endContract
                                )
                            },
                            enabled = true,
                            isOutline = false
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CancelButton(
                            onClick = {
//                        onCancelClick()
                                isEditing = false
                            },
                            label = "Cancel"
                        )
                    }

                    if (!isEditing) {
                        InfiniteTrackButton(
                            label = "Edit",
                            onClick = { isEditing = true },
                            enabled = true,
                            isOutline = false
                        )
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun EditProfilPreview() {
//    EditProfil(
//        onBackClick = {},
//        onCancelClick = {},
//        onEditClick = {},
//        user = UserModel(
//            token = "dummy-token",
//            email = "FEBRIYADI@GMAIL.COM",
//            userId = 123,
//            userName = "Neisha Salsabila",
//            userRole = "Admin",
//            address = "Some address",
//            annualBalance = 1000,
//            annualUsed = 200,
//            division = "IT",
//            greeting = "Hello, Neisha!",
//            nip_nim = "2111081011",
//            phone_number = "08123456789",
//            positionName = "Intern",
//            start_contract = "2024-01-01",
//            end_contract = "2024-12-31",
//            headprogramname = "Kak Nabila"
//        )
//    )
//}
