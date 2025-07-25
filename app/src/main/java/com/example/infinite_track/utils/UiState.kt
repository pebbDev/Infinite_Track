package com.example.infinite_track.utils

sealed class UiState<out T : Any?> {

    object Loading : UiState<Nothing>()
    object Idle : UiState<Nothing>()
    data class Success<out T : Any?>(val data: T) : UiState<T>()
    data class Error(val errorMessage: String) : UiState<Nothing>()

}