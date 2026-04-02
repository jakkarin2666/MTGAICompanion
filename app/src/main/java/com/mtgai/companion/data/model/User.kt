package com.mtgai.companion.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class User(
    @DocumentId
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val avatarUrl: String? = null,
    @ServerTimestamp
    val createdAt: Date = Date()
)
