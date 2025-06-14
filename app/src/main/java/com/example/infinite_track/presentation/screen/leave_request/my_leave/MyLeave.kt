package com.example.infinite_track.presentation.screen.leave_request.my_leave

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.infinite_track.R
import com.example.infinite_track.data.soucre.dummy.dummyMyLeaveList
import com.example.infinite_track.presentation.components.base.BaseLayout
import com.example.infinite_track.presentation.components.base.StaticBaseLayout
import com.example.infinite_track.presentation.components.button.DatePickerButton
import com.example.infinite_track.presentation.components.cards.LeaveCard
import com.example.infinite_track.presentation.core.body2
import com.example.infinite_track.presentation.core.headline2
import com.example.infinite_track.presentation.core.headline3
import com.example.infinite_track.presentation.theme.Blue_500
import com.example.infinite_track.presentation.theme.Infinite_TrackTheme
import com.example.infinite_track.presentation.theme.Purple_300
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyLeave(
    modifier: Modifier = Modifier,
) {
    val filterDateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    val displayDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    var selectedDate by remember { mutableStateOf(displayDateFormat.format(System.currentTimeMillis())) }
    val filteredLeaves = dummyMyLeaveList.filter { leave ->
        try {
            val leaveDate = leave.dateTime.split(",")[0].trim()
            val formattedLeaveDate = filterDateFormat.format(displayDateFormat.parse(leaveDate)!!)
            formattedLeaveDate == filterDateFormat.format(displayDateFormat.parse(selectedDate)!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            false
        }
    }

    Scaffold(
        modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            Row (
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "My Leave",
                    style = headline2,
                    fontWeight = FontWeight.Medium,
                    fontSize = 25.sp
                )
            }
        }
    ){ innerPadding ->


        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column {
                        DatePickerButton { newDate ->
                            selectedDate = newDate
                        }
                        Spacer(modifier = modifier.height(12.dp))
                        if (filteredLeaves.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 100.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_my_leave_empty),
                                    contentDescription = "icon my leave empty",
                                    modifier = Modifier.alpha(0.5f)
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = stringResource(R.string.time_off_request_empty),
                                    style = headline3,
                                    color = Purple_300
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = stringResource(R.string.time_off_form_prompt),
                                    style = body2,
                                    color = Purple_300
                                )
                                Text(
                                    text = "you can check here.",
                                    style = body2,
                                    color = Purple_300
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                items(filteredLeaves) { leave ->
                                    LeaveCard(
                                        myLeave = leave,
                                        onClick = { println("Card clicked!") },
                                        onDelete = { println("Card deleted!") }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}