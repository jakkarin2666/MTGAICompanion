package com.mtgai.companion.ui.screens.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtgai.companion.data.repository.ListingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateListingViewModel : ViewModel() {

    private val repository = ListingRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun createListing(
        cardId: String,
        cardName: String,
        cardImageUrl: String,
        price: Double,
        condition: String,
        description: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val listingId = repository.createListing(
                cardId = cardId,
                cardName = cardName,
                cardImageUrl = cardImageUrl,
                price = price,
                condition = condition,
                description = description
            )

            if (listingId != null) {
                _isSuccess.value = true
            } else {
                _error.value = "Failed to create listing"
            }

            _isLoading.value = false
        }
    }
}
