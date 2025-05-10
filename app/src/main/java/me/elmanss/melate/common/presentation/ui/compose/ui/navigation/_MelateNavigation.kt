package me.elmanss.melate.common.presentation.ui.compose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import me.elmanss.melate.favorites.presentation.list.compose.ListFavoritesScreen
import me.elmanss.melate.home.presentation.compose.HomeScreen

@Serializable object Home

@Serializable object Favs

// @Serializable object AddFav

@Composable
fun MelateNavHost(
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
) {
  NavHost(navController = navController, startDestination = Home, modifier = modifier) {
    composable<Home> { HomeScreen(onNavigateToFavs = { navController.navigate(Favs) }) }
    composable<Favs> { ListFavoritesScreen() }
  }
}
