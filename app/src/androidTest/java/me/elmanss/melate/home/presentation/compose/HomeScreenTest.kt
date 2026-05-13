package me.elmanss.melate.home.presentation.compose

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import java.util.concurrent.atomic.AtomicBoolean
import me.elmanss.melate.common.util.TestTags
import me.elmanss.melate.home.domain.model.SorteoModel
import me.elmanss.melate.home.presentation.entities.HomeScreenState
import me.elmanss.melate.home.presentation.entities.HomeUiEvent
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun when_sorteos_are_available_they_are_displayed() {
    val sorteos =
      listOf(
        SorteoModel(numeros = listOf(1, 2, 3, 4, 5, 6), id = "id-1", selected = false),
        SorteoModel(numeros = listOf(10, 11, 12, 13, 14, 15), id = "id-2", selected = false),
      )
    val state = HomeScreenState(sorteos = sorteos)

    composeTestRule.setContent {
      HomeScreenContent(
        uiState = state,
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = {},
      )
    }

    composeTestRule.onNodeWithText("1, 2, 3, 4, 5, 6").assertIsDisplayed()
    composeTestRule.onNodeWithText("10, 11, 12, 13, 14, 15").assertIsDisplayed()
  }

  @Test
  fun when_swiping_to_refresh_event_is_triggered() {
    val refreshTriggered = AtomicBoolean(false)
    composeTestRule.setContent {
      HomeScreenContent(
        // Include an item to ensure the LazyColumn is scrollable and captures the pull gesture
        uiState =
          HomeScreenState(sorteos = listOf(SorteoModel(listOf(1, 2, 3, 4, 5, 6), "id-0", false))),
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = { event ->
          if (event is HomeUiEvent.SwipeToRefreshSorteosEvent) {
            refreshTriggered.set(true)
          }
        },
      )
    }

    // Perform a robust swipe down on the refresh container
    composeTestRule.onNodeWithTag(TestTags.HOME_PULL_REFRESH).performTouchInput {
      swipeDown(
        startY = percentOffset(y = 0.1f).y,
        endY = percentOffset(y = 0.8f).y,
        durationMillis = 500,
      )
    }

    // Wait for the event to be processed
    composeTestRule.waitUntil(timeoutMillis = 3000) { refreshTriggered.get() }

    assert(refreshTriggered.get(), { "Refresh event was not triggered after swipe" })
  }

  @Test
  fun when_multiSelectModeEnabled_save_icon_is_visible() {
    val state = HomeScreenState(multiSelectModeEnabled = true)

    composeTestRule.setContent {
      HomeScreenContent(
        uiState = state,
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = {},
      )
    }

    composeTestRule.onNodeWithTag(TestTags.HOME_SAVE_TOPBAR_ICON).assertIsDisplayed()
  }

  @Test
  fun clicking_fab_triggers_navigation_event() {
    var navigateTriggered = false
    composeTestRule.setContent {
      HomeScreenContent(
        uiState = HomeScreenState(),
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = { event ->
          if (event is HomeUiEvent.TapGoToFavsEvent) {
            navigateTriggered = true
          }
        },
      )
    }

    composeTestRule.onNodeWithTag(TestTags.SHARED_FAB_MAIN).performClick()
    assert(navigateTriggered)
  }
}
