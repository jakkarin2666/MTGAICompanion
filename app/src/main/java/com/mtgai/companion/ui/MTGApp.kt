package com.mtgai.companion.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mtgai.companion.ui.screens.auth.LoginScreen
import com.mtgai.companion.ui.screens.auth.RegisterScreen
import com.mtgai.companion.ui.screens.deck.DeckDetailScreen
import com.mtgai.companion.ui.screens.deck.DeckListScreen
import com.mtgai.companion.ui.screens.deck.CreateDeckScreen
import com.mtgai.companion.ui.screens.market.MarketplaceScreen
import com.mtgai.companion.ui.screens.market.CreateListingScreen
import com.mtgai.companion.ui.screens.market.MyListingsScreen
import com.mtgai.companion.ui.screens.profile.ProfileScreen
import com.mtgai.companion.ui.screens.scan.ScanScreen
import com.mtgai.companion.ui.screens.card.CardDetailScreen
import com.mtgai.companion.ui.screens.auth.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object DeckList : Screen("decks")
    object DeckDetail : Screen("deck/{deckId}") {
        fun createRoute(deckId: String) = "deck/$deckId"
    }
    object CreateDeck : Screen("create_deck")
    object Scan : Screen("scan")
    object Market : Screen("market")
    object CreateListing : Screen("create_listing")
    object MyListings : Screen("my_listings")
    object Profile : Screen("profile")
    object CardDetail : Screen("card/{cardId}") {
        fun createRoute(cardId: String) = "card/$cardId"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MTGApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    val startDestination = if (isLoggedIn) Screen.Main.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onGuestLogin = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToCard = { cardId ->
                    navController.navigate(Screen.CardDetail.createRoute(cardId))
                },
                onNavigateToDeckDetail = { deckId ->
                    navController.navigate(Screen.DeckDetail.createRoute(deckId))
                },
                onNavigateToCreateDeck = {
                    navController.navigate(Screen.CreateDeck.route)
                },
                onNavigateToCreateListing = {
                    navController.navigate(Screen.CreateListing.route)
                },
                onNavigateToMyListings = {
                    navController.navigate(Screen.MyListings.route)
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.DeckDetail.route) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
            DeckDetailScreen(
                deckId = deckId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCard = { cardId ->
                    navController.navigate(Screen.CardDetail.createRoute(cardId))
                }
            )
        }

        composable(Screen.CreateDeck.route) {
            CreateDeckScreen(
                onNavigateBack = { navController.popBackStack() },
                onDeckCreated = { navController.popBackStack() }
            )
        }

        composable(Screen.CardDetail.route) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId") ?: return@composable
            CardDetailScreen(
                cardId = cardId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CreateListing.route) {
            CreateListingScreen(
                onNavigateBack = { navController.popBackStack() },
                onListingCreated = { navController.popBackStack() }
            )
        }

        composable(Screen.MyListings.route) {
            MyListingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToCard: (String) -> Unit,
    onNavigateToDeckDetail: (String) -> Unit,
    onNavigateToCreateDeck: () -> Unit,
    onNavigateToCreateListing: () -> Unit,
    onNavigateToMyListings: () -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem("Decks", Icons.Default.Home, "decks"),
        BottomNavItem("Scan", Icons.Default.Add, "scan"),
        BottomNavItem("Market", Icons.Default.Search, "market"),
        BottomNavItem("Profile", Icons.Default.Person, "profile")
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "decks",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("decks") {
                DeckListScreen(
                    onNavigateToDeck = onNavigateToDeckDetail,
                    onCreateDeck = onNavigateToCreateDeck
                )
            }
            composable("scan") {
                ScanScreen(onCardScanned = onNavigateToCard)
            }
            composable("market") {
                MarketplaceScreen(
                    onNavigateToCard = onNavigateToCard,
                    onCreateListing = onNavigateToCreateListing,
                    onMyListings = onNavigateToMyListings
                )
            }
            composable("profile") {
                ProfileScreen(onLogout = onLogout)
            }
        }
    }
}

data class BottomNavItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val route: String)
