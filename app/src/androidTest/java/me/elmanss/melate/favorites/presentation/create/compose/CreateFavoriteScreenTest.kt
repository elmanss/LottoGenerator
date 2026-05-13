package me.elmanss.melate.favorites.presentation.create.compose

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import me.elmanss.melate.common.util.TestTags
import me.elmanss.melate.favorites.presentation.create.entities.CreateFavoriteScreenState
import org.junit.Rule
import org.junit.Test

class CreateFavoriteScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun initial_state_shows_empty_input() {
    composeTestRule.setContent {
      CreateFavoriteContent(
        uiState = CreateFavoriteScreenState(),
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = {}
      )
    }

    composeTestRule.onNodeWithTag(TestTags.CREATE_INPUT_DISPLAY).assertTextEquals("")
  }

  @Test
  fun typing_numbers_updates_display() {
    val state = CreateFavoriteScreenState(keyboardInput = "12")
    
    composeTestRule.setContent {
      CreateFavoriteContent(
        uiState = state,
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = {}
      )
    }

    composeTestRule.onNodeWithTag(TestTags.CREATE_INPUT_DISPLAY).assertTextEquals("12")
  }

  @Test
  fun clicking_numpad_triggers_event() {
    var lastTypedDigit = ""
    
    composeTestRule.setContent {
      CreateFavoriteContent(
        uiState = CreateFavoriteScreenState(),
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = { event ->
           if (event is me.elmanss.melate.favorites.presentation.create.entities.CreateFavUiEvent.TapDigit) {
             lastTypedDigit = event.digit
           }
        }
      )
    }

    composeTestRule.onNodeWithTag(TestTags.CREATE_NUMPAD_7).performClick()
    assert(lastTypedDigit == "7")
  }

  @Test
  fun when_6_numbers_are_added_confirmation_dialog_appears() {
    val completeNumbers = listOf("1", "2", "3", "4", "5", "6")
    val state = CreateFavoriteScreenState(sorteoCompleted = completeNumbers)

    composeTestRule.setContent {
      CreateFavoriteContent(
        uiState = state,
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = {}
      )
    }

    composeTestRule.onNodeWithTag(TestTags.SHARED_DIALOG_CONTAINER).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.SHARED_DIALOG_CONFIRM_BTN).assertIsDisplayed()
  }
}
