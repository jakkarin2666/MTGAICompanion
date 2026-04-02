package com.mtgai.companion.ui.screens.market

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mtgai.companion.data.model.Card
import com.mtgai.companion.data.model.CardCondition
import com.mtgai.companion.data.repository.CardRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingScreen(
    onNavigateBack: () -> Unit,
    onListingCreated: () -> Unit,
    viewModel: CreateListingViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCard by remember { mutableStateOf<Card?>(null) }
    var price by remember { mutableStateOf("") }
    var selectedCondition by remember { mutableStateOf(CardCondition.NM) }
    var conditionExpanded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    val repository = remember { CardRepository() }
    var searchResults by remember { mutableStateOf<List<Card>>(emptyList()) }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            onListingCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Listing") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (selectedCard == null) {
                // Card Search
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Card Name") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (searchQuery.isNotBlank()) {
                                    isSearching = true
                                    kotlinx.coroutines.MainScope().launch {
                                        repository.searchCards(searchQuery).fold(
                                            onSuccess = { cards ->
                                                searchResults = cards.take(5)
                                                isSearching = false
                                            },
                                            onFailure = {
                                                isSearching = false
                                            }
                                        )
                                    }
                                }
                            }
                        ) {
                            if (isSearching) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                searchResults.forEach { card ->
                    Card(
                        onClick = {
                            selectedCard = card
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (card.imageUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = card.imageUrl,
                                    contentDescription = card.name,
                                    modifier = Modifier.size(50, 70),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(card.name, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "${card.setName} • $${card.price}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            } else {
                // Listing Form
                SelectedCardView(card = selectedCard!!)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Price (USD)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = conditionExpanded,
                    onExpandedChange = { conditionExpanded = !conditionExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCondition.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Condition") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = conditionExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = conditionExpanded,
                        onDismissRequest = { conditionExpanded = false }
                    ) {
                        CardCondition.entries.forEach { condition ->
                            DropdownMenuItem(
                                text = { Text(condition.displayName) },
                                onClick = {
                                    selectedCondition = condition
                                    conditionExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val priceValue = price.toDoubleOrNull() ?: 0.0
                        viewModel.createListing(
                            cardId = selectedCard!!.id,
                            cardName = selectedCard!!.name,
                            cardImageUrl = selectedCard!!.imageUrl,
                            price = priceValue,
                            condition = selectedCondition.name,
                            description = description
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isLoading && price.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Create Listing")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { selectedCard = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Change Card")
                }
            }
        }
    }
}

@Composable
fun SelectedCardView(card: Card) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (card.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = card.imageUrl,
                    contentDescription = card.name,
                    modifier = Modifier.size(80, 112),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(card.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${card.setName} • ${card.rarity}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (card.price > 0) {
                    Text(
                        "Market: $${String.format("%.2f", card.price)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

private fun kotlinx.coroutines.MainScope.launch(block: suspend () -> Unit) {
    this.launch { block() }
}
