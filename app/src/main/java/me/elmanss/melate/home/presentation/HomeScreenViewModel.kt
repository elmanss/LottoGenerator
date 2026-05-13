package me.elmanss.melate.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import logcat.logcat
import me.elmanss.melate.R
import me.elmanss.melate.home.domain.model.SorteoModel
import me.elmanss.melate.home.domain.usecase.HomeUseCases
import me.elmanss.melate.home.presentation.entities.HomeScreenSideEffect
import me.elmanss.melate.home.presentation.entities.HomeScreenState
import me.elmanss.melate.home.presentation.entities.HomeUiEvent
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val useCases: HomeUseCases) : ViewModel() {
  private val _state = MutableStateFlow(HomeScreenState())
  val state = _state.asStateFlow().stateIn(viewModelScope, SharingStarted.Lazily, HomeScreenState())

  private val _sideEffect: MutableSharedFlow<HomeScreenSideEffect?> =
    MutableSharedFlow(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
  val sideEffect = _sideEffect.asSharedFlow()

  private var clearJob: Job? = null

  fun sendEvent(event: HomeUiEvent) {
    logcat("HomeScreenVm") { event.toString() }
    when (event) {
      HomeUiEvent.SwipeToRefreshSorteosEvent -> {
        refreshSorteos()
      }

      HomeUiEvent.TapGoToFavsEvent -> {
        viewModelScope.launch {
          _state.update { state -> state.copy(multiSelectModeEnabled = false) }
          _sideEffect.emit(HomeScreenSideEffect.GoToFavs)
        }
      }

      HomeUiEvent.TapConfirmMultiSelectEvent -> {
        saveSelected()
      }

      HomeUiEvent.ExitMultiSelectEvent -> {
        launchExitMultiselect()
      }

      is HomeUiEvent.TapSorteoEvent -> {
        viewModelScope.launch {
          _sideEffect.emit(HomeScreenSideEffect.ShowSaveFavoriteDialog(event.sorteo))
        }
      }
      is HomeUiEvent.TapAddSorteoEvent -> {
        launchSaveToFavorites(event.sorteo)
      }
      is HomeUiEvent.LongTapSorteoEvent -> {
        markItemAsSelected(event.sorteo.copy(selected = true))
        _state.update { state -> state.copy(multiSelectModeEnabled = true) }
      }

      is HomeUiEvent.ToggleSorteoCheckEvent -> {
        markItemAsSelected(event.sorteo)
      }
    }
  }

  init {
    observeSorteos()
  }

  private fun observeSorteos() {
    viewModelScope.launch {
      useCases
        .fetchSorteos()
        .catch {
          logcat { "Refresh failed: ${it.message}" }
          _state.update { it.copy(isRefreshing = false) }
        }
        .collectLatest { data ->
          logcat("HomeScreenVm") { data.toString() }
          _state.update { s -> s.copy(sorteos = data, isRefreshing = false) }
        }
    }
  }

  private fun refreshSorteos() {
    viewModelScope.launch {
      _state.update { it.copy(isRefreshing = true) }

      // We re-observe to get the "new" in-memory list
      // In a real app, useCases.refresh() would update a DB and
      // the init{} observer would pick it up automatically.
      useCases.fetchSorteos().collectLatest { data ->
        _state.update { s -> s.copy(sorteos = data, isRefreshing = false) }
      }
    }
  }

  private fun launchSaveToFavorites(sorteoModel: SorteoModel) {
    viewModelScope.launch {
      useCases.saveToFavorites(sorteoModel, ZonedDateTime.now().toInstant().toEpochMilli())
      delay(250)
      _sideEffect.emit(HomeScreenSideEffect.ShowSnackBar(R.string.txt_sorteo_success))
    }
  }

  private fun markItemAsSelected(sorteo: SorteoModel) {
    _state.update { state ->
      val updatedSorteos = state.sorteos.map { if (it.id == sorteo.id) sorteo else it }
      state.copy(sorteos = updatedSorteos)
    }
  }

  private fun saveSelected() {
    viewModelScope.launch {
      val selectedSorteos = state.value.sorteos.filter { it.selected }
      logcat { "Selected sorteos: $selectedSorteos" }
      selectedSorteos
        .forEach { useCases.saveToFavorites(it, ZonedDateTime.now().toInstant().toEpochMilli()) }
        .also {
          clearSelected()
          _state.update { state -> state.copy(multiSelectModeEnabled = false) }
          _sideEffect.emit(HomeScreenSideEffect.ShowSnackBar(R.string.txt_sorteo_multi_success))
        }
    }
  }

  private fun launchExitMultiselect() {
    clearJob?.cancel()
    clearJob = viewModelScope.launch { exitMultiSelect() }
  }

  private suspend fun exitMultiSelect() {
    clearSelected()
    delay(100.milliseconds)
    _state.update { state -> state.copy(multiSelectModeEnabled = false) }
  }

  private fun clearSelected() {
    _state.update { state ->
      val clearedSorteos = state.sorteos.map { it.copy(selected = false) }
      state.copy(sorteos = clearedSorteos)
    }
  }
}
