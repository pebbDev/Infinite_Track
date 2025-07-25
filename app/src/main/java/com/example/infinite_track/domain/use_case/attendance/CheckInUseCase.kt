package com.example.infinite_track.domain.use_case.attendance

import com.example.infinite_track.domain.model.attendance.ActiveAttendanceSession
import com.example.infinite_track.domain.model.attendance.AttendanceRequestModel
import com.example.infinite_track.domain.repository.AttendanceRepository
import com.example.infinite_track.domain.use_case.location.GetCurrentCoordinatesUseCase
import com.example.infinite_track.presentation.geofencing.GeofenceManager
import javax.inject.Inject

/**
 * Use case for check-in operation following Clean Architecture principles
 * Simplified to only handle check-in process with provided target location
 * ViewModel is responsible for determining the correct target location
 */
class CheckInUseCase @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val getCurrentCoordinatesUseCase: GetCurrentCoordinatesUseCase,
    private val geofenceManager: GeofenceManager
) {
    /**
     * Performs check-in operation using real-time GPS coordinates
     * @param request The attendance request model
     * @param targetLocation The target location for geofence setup after successful check-in
     * @return Result containing ActiveAttendanceSession on success or exception on failure
     */
    suspend operator fun invoke(
        request: AttendanceRequestModel,
        targetLocation: com.example.infinite_track.domain.model.attendance.Location
    ): Result<ActiveAttendanceSession> {
        return try {
            // Step 1: Get current real-time GPS coordinates
            val coordinatesResult = getCurrentCoordinatesUseCase()

            if (coordinatesResult.isFailure) {
                return Result.failure(
                    coordinatesResult.exceptionOrNull()
                        ?: Exception("Failed to get current GPS location. Please enable location services.")
                )
            }

            val currentCoordinates = coordinatesResult.getOrNull()!!

            // Step 2: Update request with real-time coordinates
            val updatedRequest = request.copy(
                latitude = currentCoordinates.first,   // latitude from GPS
                longitude = currentCoordinates.second  // longitude from GPS
            )

            // Step 3: Call repository to perform check-in with updated coordinates
            // Backend will handle location validation
            val checkInResult = attendanceRepository.checkIn(updatedRequest)

            // Step 4: If check-in successful, setup geofence monitoring using provided target location
            if (checkInResult.isSuccess) {
                try {
                    geofenceManager.addGeofence(
                        id = targetLocation.locationId.toString(),
                        latitude = targetLocation.latitude,
                        longitude = targetLocation.longitude,
                        radius = targetLocation.radius.toFloat()
                    )
                    android.util.Log.d(
                        "CheckInUseCase",
                        "Geofence monitoring started for location ${targetLocation.locationId}"
                    )
                } catch (e: Exception) {
                    android.util.Log.e("CheckInUseCase", "Failed to setup geofence monitoring", e)
                    // Don't fail the entire check-in process if geofence setup fails
                }
            }

            checkInResult

        } catch (e: Exception) {
            android.util.Log.e("CheckInUseCase", "Error during check-in process", e)
            Result.failure(e)
        }
    }
}
