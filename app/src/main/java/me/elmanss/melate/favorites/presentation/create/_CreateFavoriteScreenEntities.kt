package me.elmanss.melate.favorites.presentation.create

data class CreateFavoriteScreenState(
  val keyboardInput: String = "", // max 2 digits, 1 - 56
  val captureError: String = "",
  val numbers: List<String> = emptyList(),
  val sorteoCompleted: List<String> = emptyList(),
  val sorteoInserted: Boolean = false,
  val sorteoStored: Boolean = false,
  val navigateBack: Boolean = false,
) {
  fun clearFlags() =
    this.copy(
      captureError = "",
      keyboardInput = "",
      numbers = emptyList(),
      sorteoCompleted = emptyList(),
      sorteoStored = false,
      sorteoInserted = false,
      navigateBack = false,
    )
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
enum class Clearable {
  BACK_NAVIGATION,
  SORTEO_COMPLETED,
  CAPTURE_NUMBER,
  ERROR,
  AFTER_STORAGE,
  MESSAGE,
}

sealed class CreateFavUiEvent {
  data class TapDigit(val digit: String) : CreateFavUiEvent()

  data object TapNext : CreateFavUiEvent()

  data object TapDelete : CreateFavUiEvent()

  data class InsertFavorite(val sorteo: List<String>) : CreateFavUiEvent()

  data object NavigateBack : CreateFavUiEvent()

  data class ClearEvent(val clearable: Clearable) : CreateFavUiEvent()

  data object ShowMessage : CreateFavUiEvent()
}
