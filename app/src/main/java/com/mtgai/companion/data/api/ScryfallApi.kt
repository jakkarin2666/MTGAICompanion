package com.mtgai.companion.data.api

import com.mtgai.companion.data.model.ScryfallCard
import com.mtgai.companion.data.model.ScryfallSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ScryfallApi {

    @GET("cards/search")
    suspend fun searchCards(
        @Query("q") query: String,
        @Query("page") page: Int = 1
    ): ScryfallSearchResponse

    @GET("cards/{id}")
    suspend fun getCard(
        @Path("id") id: String
    ): ScryfallCard

    @GET("cards/named")
    suspend fun getCardByName(
        @Query("exact") exactName: String? = null,
        @Query("fuzzy") fuzzyName: String? = null
    ): ScryfallCard

    companion object {
        const val BASE_URL = "https://api.scryfall.com/"
    }
}
