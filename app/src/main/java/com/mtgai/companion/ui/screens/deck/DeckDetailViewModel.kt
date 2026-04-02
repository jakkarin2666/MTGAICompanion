package com.mtgai.companion.ui.screens.deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtgai.companion.data.model.Deck
import com.mtgai.companion.data.model.DeckCard
import com.mtgai.companion.data.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeckDetailViewModel : ViewModel() {

    private val repository = DeckRepository()

    private val _deck = MutableStateFlow<Deck?>(null)
    val deck: StateFlow<Deck?> = _deck.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentDeckId: String? = null

    fun loadDeck(deckId: String) {
        currentDeckId = deckId
        viewModelScope.launch {
            _isLoading.value = true
            _deck.value = repository.getDeck(deckId)
            _isLoading.value = false
        }
    }

    fun addCard(card: DeckCard) {
        currentDeckId?.let { deckId ->
            viewModelScope.launch {
                repository.addCardToDeck(deckId, card)
                loadDeck(deckId)
            }
        }
    }

    fun removeCard(cardId: String) {
        currentDeckId?.let { deckId ->
            viewModelScope.launch {
                repository.removeCardFromDeck(deckId, cardId)
                loadDeck(deckId)
            }
        }
    }
}
