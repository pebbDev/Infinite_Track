package com.example.infinite_track.presentation.components.button.attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.infinite_track.domain.model.attendance.TargetLocationInfo
import com.example.infinite_track.presentation.components.search.InfiniteTrackSearchBar
import com.example.infinite_track.presentation.core.headline4
import com.example.infinite_track.presentation.theme.Infinite_TrackTheme
import com.example.infinite_track.presentation.theme.Purple_500

/**
 * Komponen utama untuk konten BottomSheet attendance yang merakit semua komponen kecil.
 *
 * @param modifier Modifier untuk styling komponen
 * @param searchValue Nilai dari search bar
 * @param searchPlaceholder Placeholder untuk search bar
 * @param targetLocationInfo Informasi lokasi target (description dan nama lokasi dari API)
 * @param currentLocationAddress Alamat lokasi saat ini
 * @param selectedWorkMode Mode kerja yang dipilih ("WFH" atau "WFA")
 * @param isBookingEnabled Apakah tombol booking dapat diklik
 * @param isCheckInEnabled Apakah tombol check-in dapat diklik
 * @param checkInButtonText Teks pada tombol check-in
 * @param outOfRangeWarningText Teks peringatan ketika di luar jangkauan
 * @param onSearchChange Callback ketika search value berubah
 * @param onModeSelected Callback ketika mode kerja dipilih
 * @param onBookingClick Callback ketika tombol booking diklik
 * @param onCheckInClick Callback ketika tombol check-in diklik
 */
@Composable
fun AttendanceBottomSheetContent(
    modifier: Modifier = Modifier,
    searchValue: String = "",
    searchPlaceholder: String = "Search location...",
    targetLocationInfo: TargetLocationInfo?,
    currentLocationAddress: String,
    selectedWorkMode: String,
    isBookingEnabled: Boolean,
    isCheckInEnabled: Boolean,
    checkInButtonText: String,
    outOfRangeWarningText: String = "Anda berada di luar jangkauan lokasi kerja ?",
    onSearchChange: (String) -> Unit = {},
    onModeSelected: (String) -> Unit,
    onBookingClick: () -> Unit,
    onCheckInClick: () -> Unit
) {
    // Transparent content - no background
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp), // Remove top padding
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Search Bar
        InfiniteTrackSearchBar(
            value = searchValue,
            placeholder = searchPlaceholder,
            onChange = onSearchChange
        )

        // Location Information Section dengan subtitle
        LocationInfoSection(
            targetLocationInfo = targetLocationInfo,
            currentLocationAddress = currentLocationAddress
        )

        // Out of Range Warning and Work Mode Selector - Always visible
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = outOfRangeWarningText,
                style = headline4,
                color = Purple_500
            )

            WorkModeSelector(
                selectedMode = selectedWorkMode,
                onModeSelected = onModeSelected
            )
        }

        // Action Buttons
        AttendanceActionButtons(
            isBookingEnabled = isBookingEnabled,
            isCheckInEnabled = isCheckInEnabled,
            checkInButtonText = checkInButtonText,
            onBookingClick = onBookingClick,
            onCheckInClick = onCheckInClick
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AttendanceBottomSheetContentPreview() {
    Infinite_TrackTheme {
        Column {
            // Preview when in range
            AttendanceBottomSheetContent(
                targetLocationInfo = TargetLocationInfo(
                    description = "Jl. Sudirman No. 123, Jakarta Pusat, DKI Jakarta",
                    locationName = "Sudirman"
                ),
                currentLocationAddress = "Jl. Thamrin No. 456, Jakarta Pusat, DKI Jakarta",
                selectedWorkMode = "WFH",
                isBookingEnabled = true,
                isCheckInEnabled = true,
                checkInButtonText = "Check In",
                onModeSelected = {},
                onBookingClick = {},
                onCheckInClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AttendanceBottomSheetContentOutOfRangePreview() {
    Infinite_TrackTheme {
        Column {
            // Preview when out of range
            AttendanceBottomSheetContent(
                targetLocationInfo = TargetLocationInfo(
                    description = "Jl. Sudirman No. 123, Jakarta Pusat, DKI Jakarta",
                    locationName = "Sudirman"
                ),
                currentLocationAddress = "Jl. Kemang No. 789, Jakarta Selatan, DKI Jakarta",
                selectedWorkMode = "WFA",
                isBookingEnabled = false,
                isCheckInEnabled = true,
                checkInButtonText = "Check In (WFA)",
                onModeSelected = {},
                onBookingClick = {},
                onCheckInClick = {}
            )
        }
    }
}
