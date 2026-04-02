package com.mtgai.companion.ui.screens.deck

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mtgai.companion.data.model.DeckFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDeckScreen(
    onNavigateBack: () -> Unit,
    onDeckCreated: () -> Unit,
    viewModel: CreateDeckViewModel = viewModel()
) {
    var deckName by remember { mutableStateOf("") }
    var selectedFormat by remember { mutableStateOf(DeckFormat.STANDARD) }
    var formatExpanded by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            onDeckCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Deck") },
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
            OutlinedTextField(
                value = deckName,
                onValueChange = { deckName = it },
                label = { Text("Deck Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = formatExpanded,
                onExpandedChange = { formatExpanded = !formatExpanded }
            ) {
                OutlinedTextField(
                    value = selectedFormat.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Format") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = formatExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = formatExpanded,
                    onDismissRequest = { formatExpanded = false }
                ) {
                    DeckFormat.entries.forEach { format ->
                        DropdownMenuItem(
                            text = { Text(format.displayName) },
                            onClick = {
                                selectedFormat = format
                                formatExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.createDeck(deckName, selectedFormat.name.lowercase())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading && deckName.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create Deck")
                }
            }
        }
    }
}
