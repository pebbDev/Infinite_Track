package com.example.infinite_track.presentation.screen.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.infinite_track.data.soucre.network.response.ContactData
import com.example.infinite_track.data.soucre.repository.contact.ContactsRepository
import com.example.infinite_track.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository
) : ViewModel() {

    private val _contactsState = MutableStateFlow<UiState<List<ContactData?>>>(UiState.Loading)
    val contactsState: StateFlow<UiState<List<ContactData?>>> get() = _contactsState

    private val _filteredContactsState = MutableStateFlow<UiState<List<ContactData?>>>(UiState.Loading)
    val filteredContactsState: StateFlow<UiState<List<ContactData?>>> get() = _filteredContactsState

    fun getAllContacts() {
        viewModelScope.launch {
            _contactsState.value = UiState.Loading
            contactsRepository.getContacts()
                .catch { e ->
                    _contactsState.value = UiState.Error(e.message.toString())
                }
                .collect { response ->
                    if (response.contactData != null) {
                        _contactsState.value = UiState.Success(response.contactData)
                    } else {
                        _contactsState.value = UiState.Error("No data found")
                    }
                }
        }
    }

    fun filterContacts(searchQuery: String) {
        viewModelScope.launch {
            _filteredContactsState.value = UiState.Loading
            delay(500)

            val query = searchQuery.trim().lowercase()
            if (query.isEmpty()) {
                _filteredContactsState.value = (_contactsState.value as? UiState.Success)?.let {
                    UiState.Success(it.data)
                } ?: UiState.Error("No data found")
            } else {
                val filteredData = (_contactsState.value as? UiState.Success)?.data?.filter { contact ->
                    contact?.name?.lowercase()?.contains(query) == true ||
                            contact?.positionName?.lowercase()?.contains(query) == true
                } ?: emptyList()
                _filteredContactsState.value = UiState.Success(filteredData)
            }
        }
    }
    fun setError(message: String) {
        _contactsState.value = UiState.Error(message)
    }
}



