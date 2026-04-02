package com.mtgai.companion.data.repository

import com.mtgai.companion.data.api.RetrofitClient
import com.mtgai.companion.data.model.Card
import com.mtgai.companion.data.model.ScryfallCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CardRepository {

    private val api = RetrofitClient.scryfallApi

    suspend fun searchCards(query: String): Result<List<Card>> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchCards(query)
            val cards = response.data.map { it.toCard() }
            Result.success(cards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCardByName(name: String): Result<Card> = withContext(Dispatchers.IO) {
        try {
            val card = api.getCardByName(fuzzyName = name).toCard()
            Result.success(card)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCardById(id: String): Result<Card> = withContext(Dispatchers.IO) {
        try {
            val card = api.getCard(id).toCard()
            Result.success(card)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun ScryfallCard.toCard(): Card = Card(
        id = id,
        name = name,
        set = set,
        setName = set_name,
        rarity = rarity,
        typeLine = type_line ?: "",
        manaCost = mana_cost ?: "",
        oracleText = oracle_text ?: "",
        imageUrl = image_uris?.normal ?: image_uris?.large ?: "",
        price = prices?.usd?.toDoubleOrNull() ?: 0.0,
        colors = colors ?: emptyList(),
        cmc = cmc ?: 0.0
    )
}
