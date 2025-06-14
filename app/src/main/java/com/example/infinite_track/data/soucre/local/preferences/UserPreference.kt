package com.example.infinite_track.data.soucre.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.infinite_track.domain.model.AttendanceState
import com.example.infinite_track.domain.model.userdata.UserModel
import com.example.infinite_track.utils.calculateNextResetTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataUserStore: DataStore<Preferences> by preferencesDataStore(name = "user")

@Singleton
class UserPreference @Inject constructor(private val dataUserStore: DataStore<Preferences>) {

    fun getUser(): Flow<UserModel> {
        return dataUserStore.data.map { preferences ->
            UserModel(
                email = preferences[EMAIL_KEY] ?: "",
                token = preferences[TOKEN_KEY] ?: "null",
                userId = preferences[USERID_KEY] ?: 0,
                userName = preferences[USERNAME_KEY] ?: "",
                userRole = preferences[USER_ROLE_KEY] ?: "",
                address = preferences[ADDRESS_KEY] ?: "",
                annualBalance = preferences[ANNUAL_BALANCE_KEY] ?: 0,
                annualUsed = preferences[ANNUAL_USED_KEY] ?: 0,
                division = preferences[DIVISION_KEY] ?: "",
                greeting = preferences[GREETING_KEY] ?: "",
                nip_nim = preferences[NIP_NIM_KEY] ?: "",
                phone_number = preferences[PHONE_NUMBER_KEY] ?: "",
                positionName = preferences[POSITION_NAME_KEY] ?: "",
                start_contract = preferences[START_CONTRACT_KEY] ?: "",
                end_contract = preferences[END_CONTRACT_KEY] ?: "",
                headprogramname = preferences[HEAD_PROGRAM_NAME_KEY]?: "",
                profilePhoto = preferences[PROFILE_PHOTO_KEY]?: ""
            )
        }
    }

    suspend fun saveUser(user: UserModel) {
        dataUserStore.edit { preferences ->
            preferences[TOKEN_KEY] = user.token ?: "null"
            preferences[EMAIL_KEY] = user.email
            preferences[USERID_KEY] = user.userId ?: 0
            preferences[USERNAME_KEY] = user.userName
            preferences[USER_ROLE_KEY] = user.userRole
            preferences[ADDRESS_KEY] = user.address ?: ""
            preferences[ANNUAL_BALANCE_KEY] = user.annualBalance ?: 0
            preferences[ANNUAL_USED_KEY] = user.annualUsed ?: 0
            preferences[DIVISION_KEY] = user.division ?: ""
            preferences[GREETING_KEY] = user.greeting
            preferences[NIP_NIM_KEY] = user.nip_nim ?: ""
            preferences[PHONE_NUMBER_KEY] = user.phone_number ?: ""
            preferences[POSITION_NAME_KEY] = user.positionName
            preferences[START_CONTRACT_KEY] = user.start_contract ?: ""
            preferences[END_CONTRACT_KEY] = user.end_contract ?: ""
            preferences[HEAD_PROGRAM_NAME_KEY] = user.headprogramname?: ""
            preferences[PROFILE_PHOTO_KEY] = user.profilePhoto?: ""
        }
    }

    suspend fun logout() {
        dataUserStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun saveAttendanceState(isAttend: Boolean, isCheckedOut: Boolean, lastCheckoutTime: Long) {
        dataUserStore.edit { preferences ->
            preferences[IS_ATTEND_KEY] = isAttend
            preferences[IS_CHECKED_OUT_KEY] = isCheckedOut
            preferences[LAST_CHECKOUT_TIME_KEY] = lastCheckoutTime
        }
    }


    fun getAttendanceState(): Flow<AttendanceState> {
        return dataUserStore.data.map { preferences ->
            AttendanceState(
                isAttend = preferences[IS_ATTEND_KEY] ?: false,
                isCheckedOut = preferences[IS_CHECKED_OUT_KEY] ?: false,
                lastCheckoutTime = preferences[LAST_CHECKOUT_TIME_KEY] ?: 0L
            )
        }
    }

    suspend fun resetAttendanceIfNeeded() {
        val lastCheckout = getAttendanceState().firstOrNull()?.lastCheckoutTime ?: return
        val resetTime = calculateNextResetTime(lastCheckout)

        if (System.currentTimeMillis() >= resetTime) {
            resetAttendance()
        }
    }

    private suspend fun resetAttendance() {
        dataUserStore.edit { preferences ->
            preferences[IS_ATTEND_KEY] = false
            preferences[IS_CHECKED_OUT_KEY] = false
        }
    }

    suspend fun updatePartialUser(
        token: String? = null,
        address: String? = null,
        phone_number: String? = null,
        nip_nim: String? = null,
        start_contract: String? = null,
        end_contract: String? = null
    ) {
        dataUserStore.edit { preferences ->
            token?.let { preferences[TOKEN_KEY] = it }
            address?.let { preferences[ADDRESS_KEY] = it }
            phone_number?.let { preferences[PHONE_KEY] = it }
            nip_nim?.let { preferences[NIP_KEY] = it }
            start_contract?.let { preferences[START_CONTRACT_KEY] = it }
            end_contract?.let { preferences[END_CONTRACT_KEY] = it }
        }
    }


    companion object {
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val USERID_KEY = intPreferencesKey("userId")
        private val USERNAME_KEY = stringPreferencesKey("userName")
        private val USER_ROLE_KEY = stringPreferencesKey("userRole")
        private val HEAD_PROGRAM_NAME_KEY = stringPreferencesKey("headprogramname")
        private val ADDRESS_KEY = stringPreferencesKey("address")
        private val ANNUAL_BALANCE_KEY = intPreferencesKey("annualBalance")
        private val ANNUAL_USED_KEY = intPreferencesKey("annualUsed")
        private val DIVISION_KEY = stringPreferencesKey("division")
        private val GREETING_KEY = stringPreferencesKey("greeting")
        private val NIP_NIM_KEY = stringPreferencesKey("nip_nim")
        private val PHONE_NUMBER_KEY = stringPreferencesKey("phone_number")
        private val POSITION_NAME_KEY = stringPreferencesKey("positionName")
        private val START_CONTRACT_KEY = stringPreferencesKey("start_contract")
        private val END_CONTRACT_KEY = stringPreferencesKey("end_contract")
        private val PROFILE_PHOTO_KEY = stringPreferencesKey("profilePhoto")
        private val IS_ATTEND_KEY = booleanPreferencesKey("is_attend")
        private val IS_CHECKED_OUT_KEY = booleanPreferencesKey("is_checked_out")
        private val LAST_CHECKOUT_TIME_KEY = longPreferencesKey("last_checkout_time")
        private val PHONE_KEY = stringPreferencesKey("phone_number")
        private val NIP_KEY = stringPreferencesKey("nip_nim")
    }
}