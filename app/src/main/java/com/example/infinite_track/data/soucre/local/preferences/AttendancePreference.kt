package com.example.infinite_track.data.soucre.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.attendanceDataStore: DataStore<Preferences> by preferencesDataStore(name = "attendance_session")

@Singleton
class AttendancePreference @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.attendanceDataStore

    companion object {
        private val ACTIVE_ATTENDANCE_ID_KEY = intPreferencesKey("active_attendance_id")
        private val IS_INSIDE_GEOFENCE_KEY = booleanPreferencesKey("is_inside_geofence")
    }

    /**
     * Save the active attendance ID to DataStore
     */
    suspend fun saveActiveAttendanceId(id: Int) {
        dataStore.edit { preferences ->
            preferences[ACTIVE_ATTENDANCE_ID_KEY] = id
        }
    }

    /**
     * Get the active attendance ID as a Flow
     */
    fun getActiveAttendanceId(): Flow<Int?> {
        return dataStore.data.map { preferences ->
            preferences[ACTIVE_ATTENDANCE_ID_KEY]
        }
    }

    /**
     * Clear the active attendance ID when checkout is successful
     */
    suspend fun clearActiveAttendanceId() {
        dataStore.edit { preferences ->
            preferences.remove(ACTIVE_ATTENDANCE_ID_KEY)
        }
    }

    /**
     * Get the user's geofence status as a Flow
     * @return Flow<Boolean> - true if user is inside geofence, false otherwise (default: false)
     */
    fun isUserInsideGeofence(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_INSIDE_GEOFENCE_KEY] ?: false
        }
    }

    /**
     * Set the user's geofence status
     * @param isInside true if user entered geofence, false if user exited
     */
    suspend fun setUserInsideGeofence(isInside: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_INSIDE_GEOFENCE_KEY] = isInside
        }
    }
}
