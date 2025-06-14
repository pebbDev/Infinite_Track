package com.example.infinite_track.presentation.components.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.infinite_track.presentation.theme.Infinite_TrackTheme

@Composable
fun InfiniteTrackSearchBar() {
    var searchValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ){
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
                IconButton(onClick = { /* TODO: Implement Search */ }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search Event")
                }
            }
        )
    }
}

@Preview
@Composable
private fun SearchPreview() {
    Infinite_TrackTheme {
        InfiniteTrackSearchBar()
    }
}