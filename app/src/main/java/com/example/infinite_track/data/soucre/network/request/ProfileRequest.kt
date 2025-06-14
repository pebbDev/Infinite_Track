package com.example.infinite_track.data.soucre.network.request

data class ProfileRequest(
    val phone_number: String,
    val nip_nim: String,
    val address: String,
    val start_contract: String?,
    val end_contract: String?
)