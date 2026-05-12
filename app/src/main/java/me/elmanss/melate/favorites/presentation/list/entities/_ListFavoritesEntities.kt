package me.elmanss.melate.favorites.presentation.list.entities

import androidx.annotation.StringRes
import me.elmanss.melate.favorites.domain.model.FavoritoModel

sealed class ListFavUiEvent {
  data object ClearFlags : ListFavUiEvent()

  data class TapDeleteFavEvent(val fav: FavoritoModel) : ListFavUiEvent()

  data object ExitMultiDelete : ListFavUiEvent()

  data class LongTapFavEvent(val fav: FavoritoModel, val index: Int) : ListFavUiEvent()

  data object TapCreateEvent : ListFavUiEvent()

  data class ToggleFavCheckEvent(val fav: FavoritoModel, val index: Int) : ListFavUiEvent()

  data class TapFavEvent(val fav: FavoritoModel) : ListFavUiEvent()

  data object TapConfirmMultiDeleteEvent : ListFavUiEvent()

  data object TapDeleteMultipleFavs : ListFavUiEvent()

  data object TapFetchFronNetworkEvent : ListFavUiEvent()

  data class ShowConnectivityMessage(val show: Boolean) : ListFavUiEvent()

  data class ToggleSubmittedEvent(val fav: FavoritoModel) : ListFavUiEvent()
}

sealed class ListFavoritesSideEffect {
  data object LaunchCreateScreen : ListFavoritesSideEffect()

  data class ShowSnackBar(@param:StringRes val messageId: Int) : ListFavoritesSideEffect()

  data class ShowDeleteDialog(val favorite: FavoritoModel) : ListFavoritesSideEffect()

  data object ShowMultiDeleteDialog : ListFavoritesSideEffect()
}

data class ListFavoritesScreenState(
  val favs: List<FavoritoModel> = emptyList(),
  val multiselectEnabled: Boolean = false,
  val isLoading: Boolean = false,
) {
  fun clearFlags(): ListFavoritesScreenState {
    return this.copy(isLoading = false)
  }
}
