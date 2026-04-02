package com.mtgai.companion.ui.screens.deck

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mtgai.companion.data.model.Deck
import com.mtgai.companion.data.model.DeckCard

@Composable
fun AISuggestionsDialog(
    deck: Deck,
    onDismiss: () -> Unit
) {
    // Simple AI suggestion logic based on deck composition
    val suggestions = generateSuggestions(deck)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Suggestions")
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Based on your ${deck.name} (${deck.format}):",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(suggestions) { suggestion ->
                    SuggestionCard(suggestion = suggestion)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun SuggestionCard(suggestion: AISuggestion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = suggestion.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = suggestion.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

data class AISuggestion(
    val title: String,
    val description: String
)

fun generateSuggestions(deck: Deck): List<AISuggestion> {
    val suggestions = mutableListOf<AISuggestion>()
    val totalCards = deck.cards.sumOf { it.quantity }

    // Check deck size
    when {
        totalCards < 60 -> suggestions.add(
            AISuggestion(
                "Deck too small",
                "Your deck has only $totalCards cards. Most formats require at least 60 cards."
            )
        )
        totalCards > 60 -> suggestions.add(
            AISuggestion(
                "Deck size optimization",
                "Your deck has $totalCards cards. Consider trimming to 60 for better consistency."
            )
        )
    }

    // Check land ratio (roughly 1/3 should be lands)
    val lands = deck.cards.filter {
        it.name.contains("Forest", ignoreCase = true) ||
        it.name.contains("Island", ignoreCase = true) ||
        it.name.contains("Swamp", ignoreCase = true) ||
        it.name.contains("Mountain", ignoreCase = true) ||
        it.name.contains("Plains", ignoreCase = true) ||
        it.name.contains("Land", ignoreCase = true)
    }.sumOf { it.quantity }

    val landRatio = if (totalCards > 0) lands.toDouble() / totalCards else 0.0

    when {
        landRatio < 0.3 -> suggestions.add(
            AISuggestion(
                "Need more lands",
                "Only ${(landRatio * 100).toInt()}% lands. Consider adding about ${((totalCards * 0.35) - lands).toInt()} more."
            )
        )
        landRatio > 0.5 -> suggestions.add(
            AISuggestion(
                "Too many lands",
                "${(landRatio * 100).toInt()}% lands is quite high. Consider trimming some."
            )
        )
    }

    if (suggestions.isEmpty()) {
        suggestions.add(
            AISuggestion(
                "Looking good!",
                "Your deck looks reasonably balanced. Keep testing and adjust based on results!"
            )
        )
    }

    return suggestions
}
