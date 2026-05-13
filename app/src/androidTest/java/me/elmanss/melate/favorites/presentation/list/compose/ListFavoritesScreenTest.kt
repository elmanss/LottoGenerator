package me.elmanss.melate.favorites.presentation.list.compose

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.longClick
import me.elmanss.melate.common.data.local.FavOrigin
import me.elmanss.melate.common.util.NetworkStatus
import me.elmanss.melate.common.util.TestTags
import me.elmanss.melate.favorites.domain.model.FavoritoModel
import me.elmanss.melate.favorites.presentation.list.entities.ListFavUiEvent
import me.elmanss.melate.favorites.presentation.list.entities.ListFavoritesScreenState
import org.junit.Rule
import org.junit.Test

class ListFavoritesScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun when_list_is_empty_empty_state_is_displayed() {
    composeTestRule.setContent {
      ListFavoritesContent(
        uiState = ListFavoritesScreenState(favs = emptyList()),
        connectivityStatus = NetworkStatus.Available,
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = {},
        formatDate = { "" }
      )
    }

    composeTestRule.onNodeWithTag(TestTags.LIST_EMPTY_STATE_CONTAINER).assertIsDisplayed()
  }

  @Test
  fun when_list_has_items_they_are_displayed() {
    val favs = listOf(
      FavoritoModel(id = 1L, sorteo = "1, 2, 3", origin = FavOrigin.Random),
      FavoritoModel(id = 2L, sorteo = "10, 11, 12", origin = FavOrigin.Manual)
    )

    composeTestRule.setContent {
      ListFavoritesContent(
        uiState = ListFavoritesScreenState(favs = favs),
        connectivityStatus = NetworkStatus.Available,
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = {},
        formatDate = { "Today" }
      )
    }

    composeTestRule.onNodeWithText("1, 2, 3").assertIsDisplayed()
    composeTestRule.onNodeWithText("10, 11, 12").assertIsDisplayed()
  }

  @Test
  fun clicking_submission_toggle_triggers_event() {
    val fav = FavoritoModel(id = 1L, sorteo = "1, 2, 3", origin = FavOrigin.Random)
    var toggleTriggered = false

    composeTestRule.setContent {
      ListFavoritesContent(
        uiState = ListFavoritesScreenState(favs = listOf(fav)),
        connectivityStatus = NetworkStatus.Available,
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = { event ->
          if (event is ListFavUiEvent.ToggleSubmittedEvent && event.fav.id == fav.id) {
            toggleTriggered = true
          }
        },
        formatDate = { "" }
      )
    }

    composeTestRule.onNodeWithTag(TestTags.FAV_ITEM_SUBMISSION_TOGGLE).performClick()
    assert(toggleTriggered)
  }

  @Test
  fun long_pressing_item_enters_multi_delete_mode() {
    val fav = FavoritoModel(id = 1L, sorteo = "1, 2, 3", origin = FavOrigin.Random)
    var longTapTriggered = false

    composeTestRule.setContent {
      ListFavoritesContent(
        uiState = ListFavoritesScreenState(favs = listOf(fav), multiselectEnabled = false),
        connectivityStatus = NetworkStatus.Available,
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = { event ->
          if (event is ListFavUiEvent.LongTapFavEvent) {
            longTapTriggered = true
          }
        },
        formatDate = { "" }
      )
    }

    composeTestRule.onNodeWithTag(TestTags.FAV_ITEM_ROW).performTouchInput {
      longClick()
    }
    
    assert(longTapTriggered)
  }

  @Test
  fun in_multi_select_mode_delete_icon_is_visible() {
    val state = ListFavoritesScreenState(
      favs = listOf(FavoritoModel(id = 1L, sorteo = "1, 2, 3", origin = FavOrigin.Random)),
      multiselectEnabled = true
    )

    composeTestRule.setContent {
      ListFavoritesContent(
        uiState = state,
        connectivityStatus = NetworkStatus.Available,
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = {},
        formatDate = { "" }
      )
    }

    composeTestRule.onNodeWithTag(TestTags.LIST_MULTI_DELETE_ICON).assertIsDisplayed()
  }
}
