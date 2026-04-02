package com.mtgai.companion.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mtgai.companion.data.model.Listing
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListingRepository {

    private val db = Firebase.firestore
    private val listingsCollection = db.collection("listings")
    private val auth = FirebaseAuth.getInstance()

    fun getAvailableListings(): Flow<List<Listing>> = callbackFlow {
        val listener = listingsCollection
            .whereEqualTo("status", "available")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val listings = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Listing::class.java)
                }?.sortedByDescending { it.createdAt } ?: emptyList()

                trySend(listings)
            }

        awaitClose { listener.remove() }
    }

    fun getMyListings(): Flow<List<Listing>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = listingsCollection
            .whereEqualTo("sellerId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val listings = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Listing::class.java)
                }?.sortedByDescending { it.createdAt } ?: emptyList()

                trySend(listings)
            }

        awaitClose { listener.remove() }
    }

    suspend fun createListing(
        cardId: String,
        cardName: String,
        cardImageUrl: String,
        price: Double,
        condition: String,
        description: String
    ): String? {
        val userId = auth.currentUser?.uid ?: return null
        val userEmail = auth.currentUser?.email ?: "Unknown"

        val listing = Listing(
            sellerId = userId,
            sellerName = userEmail.substringBefore("@"),
            cardId = cardId,
            cardName = cardName,
            cardImageUrl = cardImageUrl,
            price = price,
            condition = condition,
            description = description
        )

        return try {
            val docRef = listingsCollection.add(listing).await()
            docRef.id
        } catch (e: Exception) {
            null
        }
    }

    suspend fun markAsSold(listingId: String): Boolean {
        return try {
            listingsCollection.document(listingId)
                .update("status", "sold")
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteListing(listingId: String): Boolean {
        return try {
            listingsCollection.document(listingId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
