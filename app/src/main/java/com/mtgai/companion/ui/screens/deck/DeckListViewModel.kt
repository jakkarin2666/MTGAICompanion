package com.mtgai.companion.ui.screens.deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtgai.companion.data.model.Deck
import com.mtgai.companion.data.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeckListViewModel : ViewModel() {

    private val repository = DeckRepository()

    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decks: StateFlow<List<Deck>> = _decks.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadDecks()
    }

    private fun loadDecks() {
        viewModelScope.launch {
            repository.getDecks().collect { deckList ->
                _decks.value = deckList
                _isLoading.value = false
            }
        }
    }

    fun deleteDeck(deckId: String) {
        viewModelScope.launch {
            repository.deleteDeck(deckId)
        }
    }
}
