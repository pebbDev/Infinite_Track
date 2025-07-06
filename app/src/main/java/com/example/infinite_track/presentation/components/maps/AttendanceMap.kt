package com.example.infinite_track.presentation.components.maps

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.infinite_track.domain.model.attendance.Location
import com.example.infinite_track.domain.model.wfa.WfaRecommendation
import com.example.infinite_track.utils.MapUtils
import com.example.infinite_track.utils.PermissionUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.CameraChangedCallback

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun AttendanceMap(
    modifier: Modifier = Modifier,
    wfoLocation: Location? = null,        // Work From Office location
    wfhLocation: Location? = null,        // Work From Home location
    wfaRecommendations: List<WfaRecommendation> = emptyList(), // WFA recommendations
    selectedWfaLocation: WfaRecommendation? = null, // Selected WFA location
    targetLocation: Location? = null,     // Keep for backward compatibility
    currentUserLocation: Point? = null,
    onMarkerClick: (Location) -> Unit = {},
    onWfaMarkerClick: (WfaRecommendation) -> Unit = {}, // New callback for WFA markers
    onMapReady: (MapView) -> Unit = {},    // Added callback for when map is ready
    onCameraIdle: (Point) -> Unit = {}     // New callback for Pick on Map functionality
) {
    var mapView: MapView? by remember { mutableStateOf(null) }

    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    // Effect untuk update annotations saja - camera control diserahkan ke ViewModel
    LaunchedEffect(
        wfoLocation,
        wfhLocation,
        wfaRecommendations,
        selectedWfaLocation,
        currentUserLocation,
        mapView
    ) {
        mapView?.let { map ->
            // Small delay to ensure map is ready
            kotlinx.coroutines.delay(300)

            // Hanya update annotations, tidak ada logika kamera di sini
            MapUtils.updateMapAnnotations(
                mapView = map,
                wfoLocation = wfoLocation,
                wfhLocation = wfhLocation,
                wfaRecommendations = wfaRecommendations,
                selectedWfaLocation = selectedWfaLocation,
                currentUserLocation = currentUserLocation,
                onMarkerClick = onMarkerClick,
                onWfaMarkerClick = onWfaMarkerClick
            )

            android.util.Log.d(
                "AttendanceMap",
                "Map annotations updated - WFO: ${wfoLocation != null}, WFH: ${wfhLocation != null}, WFA: ${wfaRecommendations.size}, User: ${currentUserLocation != null}"
            )
        }
    }

    when {
        locationPermissionsState.allPermissionsGranted -> {
            // Tampilkan peta Mapbox
            AndroidView(
                factory = {
                    MapView(it).apply {
                        mapView = this // Simpan referensi MapView
                        mapboxMap.loadStyle("mapbox://styles/mapbox/outdoors-v12")
                        gestures.pitchEnabled = true
                        gestures.scrollEnabled = true
                        gestures.rotateEnabled = true
                        gestures.pinchToZoomEnabled = true
                        location.enabled = true

                        // Add camera change listener for Pick on Map functionality
                        // Use a delayed approach to detect when camera stops moving
                        var lastCameraChangeTime = 0L
                        val cameraIdleDelay = 500L // 500ms delay to detect idle

                        mapboxMap.subscribeCameraChanged { cameraChangedEventData ->
                            lastCameraChangeTime = System.currentTimeMillis()

                            // Post a delayed runnable to check if camera is still idle
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                if (System.currentTimeMillis() - lastCameraChangeTime >= cameraIdleDelay) {
                                    // Camera has been idle for the specified delay
                                    val centerPoint = mapboxMap.cameraState.center
                                    onCameraIdle(centerPoint)
                                    android.util.Log.d(
                                        "AttendanceMap",
                                        "Camera idle detected: ${centerPoint.latitude()}, ${centerPoint.longitude()}"
                                    )
                                }
                            }, cameraIdleDelay)
                        }

                        // Berikan kontrol penuh kamera ke ViewModel melalui callback
                        onMapReady.invoke(this)
                    }
                }, modifier = modifier.fillMaxSize()
            )
        }

        locationPermissionsState.shouldShowRationale -> {
            PermissionUtils.PermissionRationale(
                text = "Aplikasi ini membutuhkan izin lokasi untuk menampilkan peta dan memvalidasi absensi Anda.",
                onRequestPermission = { locationPermissionsState.launchMultiplePermissionRequest() })
        }

        else -> {
            PermissionUtils.PermissionRationale(
                text = "Izin lokasi diperlukan untuk fitur ini. Silakan aktifkan izin di pengaturan aplikasi.",
                onRequestPermission = { locationPermissionsState.launchMultiplePermissionRequest() })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AttendanceMapPreview() {
    AttendanceMap()
}