package me.elmanss.melate.common.presentation.ui.compose.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import me.elmanss.melate.favorites.presentation.create.compose.CreateFavoriteScreen
import me.elmanss.melate.favorites.presentation.list.compose.ListFavoritesScreen
import me.elmanss.melate.home.presentation.compose.HomeScreen

/**
 * Represents the different screens available in the application's navigation graph. This sealed
 * interface is used to define a type-safe set of navigation destinations.
 */
sealed interface Screen {
  data object Home : Screen

  data object Favs : Screen

  data object Create : Screen
}

/**
 * A composable that sets up the navigation display for the Lotto Generator application. It uses a
 * custom `NavDisplay` to manage screen transitions based on a back stack.
 *
 * This function defines the navigation graph and the animations used when navigating between
 * screens. The navigation is entirely managed by a [SnapshotStateList] of [Screen] objects, which
 * acts as a simple, state-driven back stack.
 *
 * The screen transitions are vertical slides:
 * - Pushing a new screen slides the new content up.
 * - Popping a screen slides the old content up and the new content down.
 *
 * @param backStack A mutable list representing the navigation back stack. Changes to this list will
 *   trigger navigation.
 */
@Composable
fun LottoGeneratorNavDisplay(backStack: SnapshotStateList<Screen>) {
  NavDisplay(
    transitionSpec = {
      ContentTransform(
        targetContentEnter =
          slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up),
        initialContentExit =
          slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Down),
      )
    },
    popTransitionSpec = {
      ContentTransform(
        targetContentEnter =
          slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Down),
        initialContentExit =
          slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up),
      )
    },
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider = { route ->
      when (route) {
        is Screen.Home ->
          NavEntry(key = route) {
            HomeScreen(onNavigateToFavs = { backStack.add(element = Screen.Favs) })
          }

        is Screen.Favs ->
          NavEntry(route) {
            ListFavoritesScreen(onCreateClicked = { backStack.add(element = Screen.Create) })
          }

        is Screen.Create -> NavEntry(key = route) { CreateFavoriteScreen() }
      }
    },
  )
}
