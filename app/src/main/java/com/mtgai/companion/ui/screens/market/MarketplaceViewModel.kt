package com.mtgai.companion.ui.screens.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtgai.companion.data.model.Listing
import com.mtgai.companion.data.repository.ListingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MarketplaceViewModel : ViewModel() {

    private val repository = ListingRepository()

    private val _listings = MutableStateFlow<List<Listing>>(emptyList())
    val listings: StateFlow<List<Listing>> = _listings.asStateFlow()

    private val _allListings = MutableStateFlow<List<Listing>>(emptyList())
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentSearchQuery = ""

    init {
        loadListings()
    }

    private fun loadListings() {
        viewModelScope.launch {
            repository.getAvailableListings().collect { listingList ->
                _allListings.value = listingList
                _listings.value = if (currentSearchQuery.isBlank()) {
                    listingList
                } else {
                    listingList.filter {
                        it.cardName.contains(currentSearchQuery, ignoreCase = true)
                    }
                }
                _isLoading.value = false
            }
        }
    }

    fun search(query: String) {
        currentSearchQuery = query
        _listings.value = if (query.isBlank()) {
            _allListings.value
        } else {
            _allListings.value.filter {
                it.cardName.contains(query, ignoreCase = true)
            }
        }
    }
}
