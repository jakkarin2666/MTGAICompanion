package com.mtgai.companion.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mtgai.companion.data.model.Deck
import com.mtgai.companion.data.model.DeckCard
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeckRepository {

    private val db = Firebase.firestore
    private val decksCollection = db.collection("decks")
    private val auth = FirebaseAuth.getInstance()

    fun getDecks(): Flow<List<Deck>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = decksCollection
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val decks = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Deck::class.java)
                } ?: emptyList()

                trySend(decks)
            }

        awaitClose { listener.remove() }
    }

    suspend fun getDeck(deckId: String): Deck? {
        return try {
            val doc = decksCollection.document(deckId).get().await()
            doc.toObject(Deck::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createDeck(name: String, format: String): String? {
        val userId = auth.currentUser?.uid ?: return null
        val deck = Deck(
            ownerId = userId,
            name = name,
            format = format,
            cards = emptyList()
        )

        return try {
            val docRef = decksCollection.add(deck).await()
            docRef.id
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateDeck(deck: Deck): Boolean {
        return try {
            decksCollection.document(deck.deckId).set(deck).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addCardToDeck(deckId: String, card: DeckCard): Boolean {
        return try {
            val deck = getDeck(deckId) ?: return false
            val existingCards = deck.cards.toMutableList()
            val existingIndex = existingCards.indexOfFirst { it.cardId == card.cardId }

            if (existingIndex >= 0) {
                val existing = existingCards[existingIndex]
                existingCards[existingIndex] = existing.copy(quantity = existing.quantity + 1)
            } else {
                existingCards.add(card)
            }

            val updatedDeck = deck.copy(cards = existingCards)
            updateDeck(updatedDeck)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeCardFromDeck(deckId: String, cardId: String): Boolean {
        return try {
            val deck = getDeck(deckId) ?: return false
            val updatedCards = deck.cards.filter { it.cardId != cardId }
            val updatedDeck = deck.copy(cards = updatedCards)
            updateDeck(updatedDeck)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteDeck(deckId: String): Boolean {
        return try {
            decksCollection.document(deckId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
