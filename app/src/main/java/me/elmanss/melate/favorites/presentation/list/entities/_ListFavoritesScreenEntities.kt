package me.elmanss.melate.favorites.presentation.list.entities

import androidx.annotation.StringRes
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

  data class ShowSnackBar(@param:StringRes val messageId: Int) : ListFavoritesSideEffect
}

sealed interface ListFavUiEvent {

  data object TapMultiDeleteEvent : ListFavUiEvent

  data object DismissDeleteFavDialog : ListFavUiEvent

  data object ExitMultiDelete : ListFavUiEvent

  data object TapConfirmMultiDeleteEvent : ListFavUiEvent

  data object DismissMultiDeleteFavDialog : ListFavUiEvent

  data object TapCreateEvent : ListFavUiEvent

  data object ClearFlags : ListFavUiEvent

  data object TapDeleteMultipleFavs : ListFavUiEvent

  data class TapFavEvent(val fav: FavoritoModel) : ListFavUiEvent

  data class TapDeleteFavEvent(val fav: FavoritoModel) : ListFavUiEvent

  data class LongTapFavEvent(val fav: FavoritoModel, val index: Int) : ListFavUiEvent

  data class ToggleFavCheckEvent(val fav: FavoritoModel, val index: Int) : ListFavUiEvent

  data class ShowConnectivityMessage(val show: Boolean) : ListFavUiEvent
}
