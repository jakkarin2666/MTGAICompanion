package com.mtgai.companion.ui.screens.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtgai.companion.data.model.Card
import com.mtgai.companion.data.repository.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardDetailViewModel : ViewModel() {

    private val repository = CardRepository()

    private val _card = MutableStateFlow<Card?>(null)
    val card: StateFlow<Card?> = _card.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadCard(cardId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getCardById(cardId).fold(
                onSuccess = { card ->
                    _card.value = card
                },
                onFailure = { e ->
                    _error.value = e.message
                }
            )

            _isLoading.value = false
        }
    }

    fun searchAndSetCard(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getCardByName(name).fold(
                onSuccess = { card ->
                    _card.value = card
                },
                onFailure = { e ->
                    _error.value = e.message
                }
            )

            _isLoading.value = false
        }
    }
}
