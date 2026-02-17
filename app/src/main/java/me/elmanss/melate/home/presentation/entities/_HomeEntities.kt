package me.elmanss.melate.home.presentation.entities

import me.elmanss.melate.home.domain.model.SorteoModel

sealed class HomeUiEvent {
  data object RefreshSorteosEvent : HomeUiEvent()

  data object DismissAddSorteoEvent : HomeUiEvent()

  data object DisableMultiSelectEvent : HomeUiEvent()

  data object ClickConfirmMultiSelectEvent : HomeUiEvent()

  data object ClickGoToFavsEvent : HomeUiEvent()

  data class ClickSorteoEvent(val sorteo: SorteoModel) : HomeUiEvent()

  data class ClickAddSorteoEvent(val sorteo: SorteoModel) : HomeUiEvent()

  data class EnableMultiSelectEvent(val sorteo: SorteoModel, val index: Int) : HomeUiEvent()

  data class SelectSorteoEvent(val sorteo: SorteoModel, val index: Int) : HomeUiEvent()
}

sealed class HomeScreenSideEffect {
  data object GoToFavs : HomeScreenSideEffect()

  data class ShowSnackBar(val message: String) : HomeScreenSideEffect()
}

data class HomeScreenState(
  val sorteos: List<SorteoModel> = emptyList(),
  val saveFaveDialogDisplayed: SorteoModel? = null,
  val multiSelectModeEnabled: Boolean = false,
)
