package com.mtgai.companion.ui.screens.deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtgai.companion.data.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateDeckViewModel : ViewModel() {

    private val repository = DeckRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun createDeck(name: String, format: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val deckId = repository.createDeck(name, format)
            if (deckId != null) {
                _isSuccess.value = true
            } else {
                _error.value = "Failed to create deck"
            }

            _isLoading.value = false
        }
    }
}
