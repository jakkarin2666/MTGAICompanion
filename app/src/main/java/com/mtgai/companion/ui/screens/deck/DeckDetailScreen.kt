package com.mtgai.companion.ui.screens.deck

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mtgai.companion.data.model.Deck
import com.mtgai.companion.data.model.DeckCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailScreen(
    deckId: String,
    onNavigateBack: () -> Unit,
    onNavigateToCard: (String) -> Unit,
    viewModel: DeckDetailViewModel = viewModel()
) {
    val deck by viewModel.deck.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showAddCardDialog by remember { mutableStateOf(false) }
    var showAISuggestions by remember { mutableStateOf(false) }

    LaunchedEffect(deckId) {
        viewModel.loadDeck(deckId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(deck?.name ?: "Deck") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAISuggestions = true }) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI Suggestions")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddCardDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Card")
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (deck == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Deck not found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    DeckStats(deck = deck!!)
                }

                item {
                    Text(
                        text = "Cards (${deck!!.cards.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }

                if (deck!!.cards.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🃏", style = MaterialTheme.typography.displayMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "No cards yet",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }

                items(deck!!.cards, key = { it.cardId }) { card ->
                    DeckCardItem(
                        card = card,
                        onClick = { onNavigateToCard(card.cardId) },
                        onRemove = { viewModel.removeCard(card.cardId) }
                    )
                }
            }
        }
    }

    if (showAddCardDialog) {
        AddCardDialog(
            onDismiss = { showAddCardDialog = false },
            onCardSelected = { card ->
                viewModel.addCard(card)
                showAddCardDialog = false
            }
        )
    }

    if (showAISuggestions && deck != null) {
        AISuggestionsDialog(
            deck = deck!!,
            onDismiss = { showAISuggestions = false }
        )
    }
}

@Composable
fun DeckStats(deck: Deck) {
    val totalCards = deck.cards.sumOf { it.quantity }
    val manaCurve = deck.cards.groupBy { it.name.takeWhile { c -> c.isDigit() }.toIntOrNull() ?: 0 }
        .toSortedMap()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${deck.format.uppercase()} • $totalCards cards",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DeckCardItem(
    card: DeckCard,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (card.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = card.imageUrl,
                    contentDescription = card.name,
                    modifier = Modifier.size(60.dp, 84.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp, 84.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🃏", style = MaterialTheme.typography.titleLarge)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "×${card.quantity}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
