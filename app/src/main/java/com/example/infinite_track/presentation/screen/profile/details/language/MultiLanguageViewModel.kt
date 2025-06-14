package com.example.infinite_track.presentation.screen.profile.details.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infinite_track.data.soucre.repository.language.LocalizationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MultiLanguageViewModel @Inject constructor(
    private val localizationRepository: LocalizationRepository
) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow("en")
    val selectedLanguage: StateFlow<String> get() = _selectedLanguage

    init {
        viewModelScope.launch {
            localizationRepository.getSelectedLanguage().collect { language ->
                _selectedLanguage.value = language
            }
        }
    }

    fun updateLanguage(language: String) {
        _selectedLanguage.value = language
        viewModelScope.launch {
            localizationRepository.setSelectedLanguage(language)
        }
    }
}

