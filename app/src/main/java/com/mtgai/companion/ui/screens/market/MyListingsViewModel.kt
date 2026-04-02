package com.mtgai.companion.ui.screens.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtgai.companion.data.model.Listing
import com.mtgai.companion.data.repository.ListingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyListingsViewModel : ViewModel() {

    private val repository = ListingRepository()

    private val _listings = MutableStateFlow<List<Listing>>(emptyList())
    val listings: StateFlow<List<Listing>> = _listings.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadListings()
    }

    private fun loadListings() {
        viewModelScope.launch {
            repository.getMyListings().collect { listingList ->
                _listings.value = listingList
                _isLoading.value = false
            }
        }
    }

    fun markAsSold(listingId: String) {
        viewModelScope.launch {
            repository.markAsSold(listingId)
        }
    }

    fun deleteListing(listingId: String) {
        viewModelScope.launch {
            repository.deleteListing(listingId)
        }
    }
}
