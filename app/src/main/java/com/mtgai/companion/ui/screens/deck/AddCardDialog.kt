package com.mtgai.companion.ui.screens.deck

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mtgai.companion.data.model.Card
import com.mtgai.companion.data.model.DeckCard
import com.mtgai.companion.data.repository.CardRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onCardSelected: (DeckCard) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Card>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val repository = remember { CardRepository() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Card to Deck") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Card Name") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (searchQuery.isNotBlank()) {
                                    isSearching = true
                                    error = null
                                    kotlinx.coroutines.MainScope().launch {
                                        repository.searchCards(searchQuery).fold(
                                            onSuccess = { cards ->
                                                searchResults = cards.take(10)
                                                isSearching = false
                                            },
                                            onFailure = { e ->
                                                error = e.message
                                                isSearching = false
                                            }
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (isSearching) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(searchResults) { card ->
                        CardSearchResult(
                            card = card,
                            onClick = {
                                onCardSelected(
                                    DeckCard(
                                        cardId = card.id,
                                        name = card.name,
                                        imageUrl = card.imageUrl,
                                        quantity = 1
                                    )
                                )
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CardSearchResult(
    card: Card,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (card.imageUrl.isNotEmpty()) {
            AsyncImage(
                model = card.imageUrl,
                contentDescription = card.name,
                modifier = Modifier.size(50, 70),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier.size(50, 70),
                contentAlignment = Alignment.Center
            ) {
                Text("🃏")
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = card.name,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${card.setName} • ${card.rarity}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun kotlinx.coroutines.MainScope() = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
