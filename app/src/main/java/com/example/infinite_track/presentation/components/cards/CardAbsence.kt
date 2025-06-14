package com.example.infinite_track.presentation.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.infinite_track.R
import com.example.infinite_track.presentation.core.body2
import com.example.infinite_track.presentation.core.headline3
import com.example.infinite_track.presentation.theme.Infinite_TrackTheme

@Composable
fun CardAbsence(
    modifier: Modifier = Modifier,
    cardTitle: String,
    cardText: String,
    cardImage: Int,
    onClick: () -> Unit
) {
    var isOnPressed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .height(80.dp)
            .width(178.dp)
            .padding(bottom = 1.5.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isOnPressed = true // Kartu ditekan
                        tryAwaitRelease() // Tunggu sampai dilepas
                        isOnPressed = false
                    },
                    onTap = {
                        onClick()
                    }
                )
            }
            .then(
                if (isOnPressed) {
                    Modifier
                        .shadow(
                            elevation = (5.dp),
                            shape = RoundedCornerShape(14.dp),
                            ambientColor = Color(0xFF8A3DFF), // Warna bayangan ungu
                            spotColor = Color(0xFF8A3DFF),
                        )
//                        .background(Color(0xFFFFFFFF))
                } else {
                    Modifier
                }
            )
            .clip(RoundedCornerShape(0.dp)) // Memastikan bayangan berada di dalam batas
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(Color(0xFFFFFFFF)),
            border = BorderStroke(0.5.dp, Color(0xFFFFFFFF)),
            modifier = Modifier
                .height(75.dp)
                .width(178.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                ) {
                    Row {
                        Text(
                            text = cardTitle,
                            style = headline3,
                            modifier = Modifier
                                .padding(start = 10.dp, top = 6.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            painter = painterResource(id = cardImage),
                            contentDescription = "Icon CheckIn",
                            modifier = Modifier
                                .padding(end = 10.dp, top = 8.dp)
                        )
                    }
                    Text(
                        text = cardText,
                        color = Color(0xffBFBBBF),
                        style = body2,
                        modifier = Modifier
                            .padding(start = 10.dp, top = 16.dp)
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun CardAbsencePreview() {
    Infinite_TrackTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
//            containerColor = Color(0xCCFFFFFF),
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
//                        .background(Color(0xCCFFFFFF))
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
//                    BaseLayout()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
//                            .verticalScroll(rememberScrollState())
                            .statusBarsPadding()
                            .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 12.dp)
                    ) {
                        Row {
                            Column {
                                CardAbsence(
                                    cardTitle = "07 : 00",
                                    cardText = "Checked In",
                                    cardImage = R.drawable.ic_checkin,
                                    onClick = { println("Card 1 clicked!") }
                                )
//                                Spacer(modifier = Modifier.height(2.dp))
                                CardAbsence(
                                    cardTitle = "10 Day",
                                    cardText = "Absence",
                                    cardImage = R.drawable.ic_absence,
                                    onClick = { println("Card 2 clicked!") }
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                CardAbsence(
                                    cardTitle = "15 : 00",
                                    cardText = "Checked Out",
                                    cardImage = R.drawable.ic_checkout,
                                    onClick = { println("Card 3 clicked!") }
                                )
//                                Spacer(modifier = Modifier.height(2.dp))
                                CardAbsence(
                                    cardTitle = "15 Day",
                                    cardText = "Total Attended",
                                    cardImage = R.drawable.ic_total_absence,
                                    onClick = { println("Card 4 clicked!") }
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}
