package me.elmanss.melate.favorites.presentation.create.entities

import androidx.annotation.StringRes

data class CreateFavoriteScreenState(
  val keyboardInput: String = "", // max 2 digits, 1 - 56
  val numbers: List<String> = emptyList(),
  val sorteoCompleted: List<String> = emptyList(),
) {
  fun clear() = this.copy(keyboardInput = "", numbers = emptyList(), sorteoCompleted = emptyList())
}

/*
   Input can store two numeric characters max.
   If add is tapped and input is a numeric string between 1 and 56, input is added to numbers, and then removed.
   If numbers reaches a size of 6, user is prompted to save the list as a Favorite model.
   If favorite is saved, clear input and number list in order to start again.

   if back is tapped:
       If input has two chars, remove last char
       if input has one char, clear input
       if input is empty, remove last item in numbers
*/

sealed class CreateFavUiEvent {
  data class TapDigit(val digit: String) : CreateFavUiEvent()

  data object TapNext : CreateFavUiEvent()

  data object TapDelete : CreateFavUiEvent()

  data class TapConfirmAdd(val sorteo: List<String>) : CreateFavUiEvent()

  data object DismissCreationDialog : CreateFavUiEvent()
}

sealed interface CreateFavSideEffect {

  data object NavigateBack : CreateFavSideEffect

  data class ShowSnackbar(@param:StringRes val messageId: Int) : CreateFavSideEffect
}
