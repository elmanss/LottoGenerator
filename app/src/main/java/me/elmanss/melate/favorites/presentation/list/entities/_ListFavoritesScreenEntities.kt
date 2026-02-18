package me.elmanss.melate.favorites.presentation.list.entities

import me.elmanss.melate.favorites.domain.model.FavoritoModel

data class ListFavoritesScreenState(
  val favs: List<FavoritoModel> = emptyList(),
  val clickedFav: FavoritoModel? = null,
  val showMultiDeletionPrompt: Boolean = false,
  val multiselectEnabled: Boolean = false,
  val isLoading: Boolean = false,
) {
  fun clearFlags() =
    this.copy(
      clickedFav = null,
      showMultiDeletionPrompt = false,
      multiselectEnabled = false,
      isLoading = false,
    )
}

sealed interface ListFavoritesSideEffect {

  data object OnMultiDeleteCompleted : ListFavoritesSideEffect

  data object LaunchCreateScreen : ListFavoritesSideEffect

  data class ShowSnackBar(val message: String) : ListFavoritesSideEffect
}

sealed interface ListFavUiEvent {

  data object ClickMultiDeleteEvent : ListFavUiEvent

  data object HideDeleteFavDialog : ListFavUiEvent

  data object DisableMultiDelete : ListFavUiEvent

  data object ClickConfirmMultiDeleteEvent : ListFavUiEvent

  data object HideMultiDeleteFavDialog : ListFavUiEvent

  data object ClickCreateEvent : ListFavUiEvent

  data object ClearFlags : ListFavUiEvent

  data object DeleteMultipleFavs : ListFavUiEvent

  data object HideSuccessMessage : ListFavUiEvent

  data class ClickFavEvent(val fav: FavoritoModel) : ListFavUiEvent

  data class ClickDeleteFavEvent(val fav: FavoritoModel) : ListFavUiEvent

  data class LongClickFavEvent(val fav: FavoritoModel, val index: Int) : ListFavUiEvent

  data class SelectFavEvent(val fav: FavoritoModel, val index: Int) : ListFavUiEvent

  data class ShowConnectivityMessage(val show: Boolean) : ListFavUiEvent
}
