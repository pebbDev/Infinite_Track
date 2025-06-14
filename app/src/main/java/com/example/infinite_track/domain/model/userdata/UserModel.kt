package com.example.infinite_track.domain.model.userdata

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val email: String,
    val token: String? ="",
    val userId: Int? = null,
    val userName: String,
    val userRole: String,
    val address: String?,
    val annualBalance: Int?= null,
    val annualUsed: Int?= null,
    val headprogramname: String?= null,
    val division: String?= "",
    val greeting: String,
    val nip_nim: String? = "",
    val phone_number: String? = "",
    val positionName: String,
    val start_contract: String? = "",
    val end_contract: String? = "",

    @field:SerializedName("profilePhoto")
    val profilePhoto: String? = null
): Parcelable