package com.mtgai.companion.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Deck(
    @DocumentId
    val deckId: String = "",
    val ownerId: String = "",
    val name: String = "",
    val format: String = "standard",
    val cards: List<DeckCard> = emptyList(),
    @ServerTimestamp
    val createdAt: Date = Date(),
    @ServerTimestamp
    val updatedAt: Date = Date()
)

enum class DeckFormat(val displayName: String) {
    STANDARD("Standard"),
    MODERN("Modern"),
    PIONEER("Pioneer"),
    LEGACY("Legacy"),
    VINTAGE("Vintage"),
    COMMANDER("Commander"),
    PAUPER("Pauper"),
    BRAWL("Brawl")
}
