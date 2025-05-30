package me.elmanss.melate.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import logcat.logcat
import me.elmanss.melate.home.domain.model.SorteoModel
import me.elmanss.melate.home.domain.usecase.HomeUseCases
import java.time.ZonedDateTime
import javax.inject.Inject

sealed class HomeUiEvent {
  data object RefreshSorteos : HomeUiEvent()

  data class ShowSaveSorteoDialog(val sorteo: SorteoModel) : HomeUiEvent()

  data object HideSaveSorteoDialog : HomeUiEvent()

  data class ConfirmSaveSorteo(val sorteo: SorteoModel) : HomeUiEvent()

  data class EnableSorteoMultiSelect(val sorteo: SorteoModel, val index: Int) : HomeUiEvent()

  data class SelectSorteo(val sorteo: SorteoModel, val index: Int) : HomeUiEvent()

  data object ConfirmMultiSelect : HomeUiEvent()

  data object GoToFavs : HomeUiEvent()

  object ExitMultiSelect : HomeUiEvent()

  data class DisplaySuccessMessage(val visible: Boolean) : HomeUiEvent()

  object ClearFlags : HomeUiEvent()
}

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val useCases: HomeUseCases) : ViewModel() {
  private val _state = MutableStateFlow(HomeScreenState())
  val state = _state.asStateFlow().stateIn(viewModelScope, SharingStarted.Lazily, HomeScreenState())
  private var fetchJob: Job? = null

  fun sendEvent(event: HomeUiEvent) {
    when (event) {
      HomeUiEvent.RefreshSorteos -> {
        launchFetchSorteos()
      }

      HomeUiEvent.GoToFavs -> {
        _state.update { state -> state.copy(onGoToFav = true) }
      }

      HomeUiEvent.ConfirmMultiSelect -> {
        saveSelected()
      }

      is HomeUiEvent.DisplaySuccessMessage -> {
        showSuccessMsg(event.visible)
      }
      is HomeUiEvent.ShowSaveSorteoDialog -> {
        showWarning(event.sorteo)
      }
      HomeUiEvent.HideSaveSorteoDialog -> {
        showWarning()
      }
      is HomeUiEvent.ConfirmSaveSorteo -> {
        launchSaveToFavorites(event.sorteo)
      }
      is HomeUiEvent.EnableSorteoMultiSelect -> {
        markItemAsSelected(event.sorteo, event.index)
        _state.update { state -> state.copy(multiSelectMode = true) }
      }

      is HomeUiEvent.SelectSorteo -> {
        markItemAsSelected(event.sorteo, event.index)
      }

      HomeUiEvent.ExitMultiSelect -> {
        _state.update { state -> state.copy(multiSelectMode = false) }
      }

      HomeUiEvent.ClearFlags -> {
        _state.update { state -> state.clearFlags() }
      }
    }
  }

  init {
    launchFetchSorteos()
  }

  private fun launchFetchSorteos() {
    fetchJob?.cancel()
    fetchJob = viewModelScope.launch { fetchSorteos() }
  }

  fun getListId() = useCases.getListId()

  private suspend fun fetchSorteos() {
    useCases.fetchSorteos().collectLatest {
      logcat("HomeScreenVm") { it.toString() }
      _state.update { s -> s.copy(sorteos = it) }
    }
  }

  private fun launchSaveToFavorites(sorteoModel: SorteoModel) {
    viewModelScope.launch {
      useCases.saveToFavorites(sorteoModel, ZonedDateTime.now().toInstant().toEpochMilli())
      delay(250)
      dismissWarning()
      showSuccessMsg(true)
    }
  }

  private fun showWarning(sorteo: SorteoModel? = null) {
    logcat { "clicked sorteo" }
    _state.update { state -> state.copy(isWarningShown = true, clickedSorteo = sorteo) }
  }

  private fun dismissWarning() {
    _state.update { state -> state.copy(isWarningShown = false, clickedSorteo = null) }
  }

  private fun showSuccessMsg(show: Boolean) {
    _state.update { state -> state.copy(showStorageSuccess = show) }
  }

  private fun markItemAsSelected(sorteo: SorteoModel, index: Int) {
    val currentSorteosMutable = state.value.sorteos.toMutableList()
    currentSorteosMutable[index] = sorteo
    _state.update { state -> state.copy(sorteos = currentSorteosMutable) }
  }

  private fun saveSelected() {
    viewModelScope.launch {
      val selectedSorteos = state.value.sorteos.filter { it.selected }
      logcat { "Selected sorteos: $selectedSorteos" }
      selectedSorteos
        .forEach { useCases.saveToFavorites(it, ZonedDateTime.now().toInstant().toEpochMilli()) }
        .also {
          clearSelected()
          _state.update { state -> state.copy(multiSelectMode = false) }
        }
    }
  }

  private fun clearSelected() {
    val clearedSorteos = state.value.sorteos.onEach { if (it.selected) it.selected = false }
    logcat { "Cleared sorteos: ${clearedSorteos}" }
    _state.update { state -> state.copy(sorteos = clearedSorteos) }
  }
}
