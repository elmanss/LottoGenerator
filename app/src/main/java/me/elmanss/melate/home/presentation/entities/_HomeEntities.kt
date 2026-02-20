package me.elmanss.melate.home.presentation.entities

import androidx.annotation.StringRes
import me.elmanss.melate.home.domain.model.SorteoModel

sealed class HomeUiEvent {
  data object SwipeToRefreshSorteosEvent : HomeUiEvent()

  data object ExitMultiSelectEvent : HomeUiEvent()

  data object TapConfirmMultiSelectEvent : HomeUiEvent()

  data object TapGoToFavsEvent : HomeUiEvent()

  data class TapSorteoEvent(val sorteo: SorteoModel) : HomeUiEvent()

  data class TapAddSorteoEvent(val sorteo: SorteoModel) : HomeUiEvent()

  data class LongTapSorteoEvent(val sorteo: SorteoModel, val index: Int) : HomeUiEvent()

  data class ToggleSorteoCheckEvent(val sorteo: SorteoModel, val index: Int) : HomeUiEvent()
}

sealed class HomeScreenSideEffect {
  data object GoToFavs : HomeScreenSideEffect()

  data class ShowSnackBar(@param:StringRes val messageId: Int) : HomeScreenSideEffect()

  data class ShowSaveFavoriteDialog(val sorteo: SorteoModel) : HomeScreenSideEffect()
}

data class HomeScreenState(
  val sorteos: List<SorteoModel> = emptyList(),
  val multiSelectModeEnabled: Boolean = false,
)
