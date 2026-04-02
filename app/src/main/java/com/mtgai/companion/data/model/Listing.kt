package com.mtgai.companion.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Listing(
    @DocumentId
    val listingId: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val cardId: String = "",
    val cardName: String = "",
    val cardImageUrl: String = "",
    val price: Double = 0.0,
    val condition: String = "NM",
    val status: String = "available",
    val description: String = "",
    @ServerTimestamp
    val createdAt: Date = Date()
)

enum class CardCondition(val displayName: String) {
    NM("Near Mint"),
    LP("Lightly Played"),
    MP("Moderately Played"),
    HP("Heavily Played")
}

data class Conversation(
    @DocumentId
    val conversationId: String = "",
    val buyerId: String = "",
    val sellerId: String = "",
    val listingId: String = "",
    val listingTitle: String = "",
    @ServerTimestamp
    val lastMessageAt: Date = Date()
)

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val text: String = "",
    @ServerTimestamp
    val timestamp: Date = Date()
)
