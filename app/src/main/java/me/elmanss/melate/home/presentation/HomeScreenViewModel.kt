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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import logcat.logcat
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

  private var fetchJob: Job? = null
  private var clearJob: Job? = null

  fun sendEvent(event: HomeUiEvent) {
    logcat("HomeScreenVm") { event.toString() }
    when (event) {
      HomeUiEvent.RefreshSorteosEvent -> {
        launchFetchSorteos()
      }

      HomeUiEvent.ClickGoToFavsEvent -> {
        viewModelScope.launch {
          _state.update { state -> state.copy(multiSelectMode = false) }
          _sideEffect.emit(HomeScreenSideEffect.GoToFavs)
        }
      }

      HomeUiEvent.ClickConfirmMultiSelectEvent -> {
        saveSelected()
      }

      HomeUiEvent.ClearFlags -> {
        _state.update { state -> state.clearFlags() }
      }

      HomeUiEvent.DisableMultiSelectEvent -> {
        launchExitMultiselect()
      }

      is HomeUiEvent.ClickSorteoEvent -> {
        _state.update { state -> state.copy(saveFaveDialogDisplayed = event.sorteo) }
      }
      is HomeUiEvent.ClickAddSorteoEvent -> {
        launchSaveToFavorites(event.sorteo)
      }
      is HomeUiEvent.DismissAddSorteoEvent -> {
        _state.update { state -> state.copy(saveFaveDialogDisplayed = null) }
      }
      is HomeUiEvent.EnableMultiSelectEvent -> {
        markItemAsSelected(event.sorteo, event.index)
        _state.update { state -> state.copy(multiSelectMode = true) }
      }

      is HomeUiEvent.SelectSorteoEvent -> {
        markItemAsSelected(event.sorteo, event.index)
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
      sendEvent(HomeUiEvent.DismissAddSorteoEvent)
      _sideEffect.emit(HomeScreenSideEffect.ShowSnackBar("Sorteo guardado en favoritos"))
    }
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
          _sideEffect.emit(HomeScreenSideEffect.ShowSnackBar("Sorteos almacenados exitosamente."))
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
    _state.update { state -> state.copy(multiSelectMode = false) }
  }

  private fun clearSelected() {
    val clearedSorteos = state.value.sorteos.onEach { if (it.selected) it.selected = false }
    logcat { "Cleared sorteos: $clearedSorteos" }
    _state.update { state -> state.copy(sorteos = clearedSorteos) }
  }
}
