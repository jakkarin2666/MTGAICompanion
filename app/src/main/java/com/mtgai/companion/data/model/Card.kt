package com.mtgai.companion.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Card(
    val id: String = "",
    val name: String = "",
    val set: String = "",
    val setName: String = "",
    val rarity: String = "",
    val typeLine: String = "",
    val manaCost: String = "",
    val oracleText: String = "",
    val imageUrl: String = "",
    val price: Double = 0.0,
    val colors: List<String> = emptyList(),
    val cmc: Double = 0.0
)

// Scryfall API Response
data class ScryfallCard(
    val id: String,
    val name: String,
    val set: String,
    val set_name: String,
    val rarity: String,
    val type_line: String,
    val mana_cost: String?,
    val oracle_text: String?,
    val image_uris: ScryfallImageUris?,
    val prices: ScryfallPrices?,
    val colors: List<String>?,
    val cmc: Double?
)

data class ScryfallImageUris(
    val small: String?,
    val normal: String?,
    val large: String?,
    val png: String?,
    val art_crop: String?,
    val border_crop: String?
)

data class ScryfallPrices(
    val usd: String?,
    val usd_foil: String?,
    val usd_etched: String?,
    val eur: String?,
    val eur_foil: String?
)

data class ScryfallSearchResponse(
    val data: List<ScryfallCard>,
    val has_more: Boolean,
    val next_page: String?
)

// Firestore Card in Deck
data class DeckCard(
    val cardId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val quantity: Int = 1
)
