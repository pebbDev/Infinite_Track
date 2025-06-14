package com.example.infinite_track.presentation.screen.contact

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.infinite_track.presentation.components.base.StaticBaseLayout
import com.example.infinite_track.presentation.components.cards.ContactCard
import com.example.infinite_track.presentation.components.empty.EmptyListAnimation
import com.example.infinite_track.presentation.components.empty.ErrorAnimation
import com.example.infinite_track.presentation.components.loading.LoadingAnimation
import com.example.infinite_track.presentation.core.headline2
import com.example.infinite_track.presentation.core.headline4
import com.example.infinite_track.utils.UiState
import com.example.infinite_track.utils.isInternetAvailable

@Composable
fun ContactScreen(viewModel: ContactsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val contactsState by viewModel.contactsState.collectAsState()
    val filteredContactsState by viewModel.filteredContactsState.collectAsState()

    var searchValue by remember { mutableStateOf("") }

    if (contactsState is UiState.Loading) {
        LoadingAnimation()
    }

    if (filteredContactsState is UiState.Loading) {
        LoadingAnimation() // Animasi loading
    }

    // Ambil semua kontak saat pertama kali screen muncul
    LaunchedEffect(Unit) {
        if (isInternetAvailable(context)) {
            viewModel.getAllContacts()
        } else {
            viewModel.setError("Tidak ada koneksi internet")
        }
    }

// Filter saat searchValue berubah
    LaunchedEffect(searchValue) {
        if (isInternetAvailable(context)) {
            viewModel.filterContacts(searchValue)
        } else {
            viewModel.setError("Tidak ada koneksi internet")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Employees",
                    style = headline2,
                    fontWeight = FontWeight.Medium,
                    fontSize = 25.sp
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = (filteredContactsState as? UiState.Success)?.data?.size?.toString()
                        ?: "0",
                    style = headline2,
                    fontWeight = FontWeight.Medium,
                    fontSize = 25.sp
                )
            }
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
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Search Bar
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    value = searchValue,
                    onValueChange = { newValue -> searchValue = newValue },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    leadingIcon = {
                        IconButton(onClick = { /* Implement Search Icon */ }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Contacts"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Menampilkan hasil kontak setelah filter atau load selesai
                when (filteredContactsState) {
                    is UiState.Loading -> {
                        LoadingAnimation()
                    }

                    is UiState.Success -> {
                        val contacts = (filteredContactsState as UiState.Success).data
                        if (contacts.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                items(contacts) { contact ->
                                    val fullImageUrl = if (!contact?.profilePhoto.isNullOrEmpty()) {
                                        contact?.profilePhoto
                                    } else {
                                        "https://w7.pngwing.com/pngs/177/551/png-transparent-user-interface-design-computer-icons-default-stephen-salazar-graphy-user-interface-design-computer-wallpaper-sphere-thumbnail.png"
                                    }

                                    ContactCard(
                                        name = contact?.name ?: "-",
                                        position = contact?.positionName ?: "-",
                                        cardImage = fullImageUrl,
                                        phone = contact?.actions?.call ,
                                        message = contact?.actions?.email ,
                                        whatsapp = contact?.actions?.whatsapp ,
                                        onClickCard = {},
                                        messageWA = "Halo ${contact?.name}",
                                        context = context
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    EmptyListAnimation(modifier = Modifier.size(150.dp))
                                    Text(
                                        text = ("No contacts found matching \"$searchValue\""),
                                        style = headline4,
                                    )
                                }
                            }
                        }
                    }

                    is UiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                ErrorAnimation(modifier = Modifier.size(150.dp))
                                Text(
                                    text = (filteredContactsState as UiState.Error).errorMessage,
                                    style = headline4,
                                    color = Color.Red
                                )
                            }
                        }
                    }

                    is UiState.Idle -> {

                    }
                }
            }
        }
    }
}



