package me.elmanss.melate.favorites.presentation.list

import androidx.annotation.StringRes
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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import logcat.logcat
import me.elmanss.melate.R
import me.elmanss.melate.common.util.NetworkConnectivityObserver
import me.elmanss.melate.favorites.domain.model.FavoritoModel
import me.elmanss.melate.favorites.domain.usecase.FavoritesUseCases
import me.elmanss.melate.favorites.presentation.list.entities.ListFavUiEvent
import me.elmanss.melate.favorites.presentation.list.entities.ListFavoritesScreenState
import me.elmanss.melate.favorites.presentation.list.entities.ListFavoritesSideEffect
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ListFavoritesScreenViewModel
@Inject
constructor(
  private val useCases: FavoritesUseCases,
  connectivityObserver: NetworkConnectivityObserver,
) : ViewModel() {
  private val _state = MutableStateFlow(ListFavoritesScreenState())
  val state =
    _state.asStateFlow().stateIn(viewModelScope, SharingStarted.Eagerly, ListFavoritesScreenState())

  private val _sideEffect: MutableSharedFlow<ListFavoritesSideEffect?> =
    MutableSharedFlow(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  val sideEffect = _sideEffect.asSharedFlow()
  val connectivity = connectivityObserver.networkStatus

  private var fetchJob: Job? = null

  init {
    launchFetchFavorites()
  }

  fun sendEvent(event: ListFavUiEvent) {
    when (event) {
      ListFavUiEvent.ClearFlags -> {
        _state.update { state -> state.clearFlags() }
      }
      is ListFavUiEvent.TapDeleteFavEvent -> {
        deleteFavs(event.fav)
      }
      ListFavUiEvent.ExitMultiDelete -> {
        clearSelected()
      }
      is ListFavUiEvent.LongTapFavEvent -> {
        markItemAsSelected(event.fav, event.index)
        _state.update { state -> state.copy(multiselectEnabled = true) }
      }

      ListFavUiEvent.TapCreateEvent -> {
        viewModelScope.launch { _sideEffect.emit(ListFavoritesSideEffect.LaunchCreateScreen) }
      }
      ListFavUiEvent.DismissDeleteFavDialog -> {
        showWarning()
      }
      is ListFavUiEvent.ToggleFavCheckEvent -> {
        markItemAsSelected(event.fav, event.index)
      }
      is ListFavUiEvent.TapFavEvent -> {
        showWarning(event.fav)
      }
      is ListFavUiEvent.TapConfirmMultiDeleteEvent -> {
        showMultideletionPrompt(true)
      }
      is ListFavUiEvent.DismissMultiDeleteFavDialog -> {
        showMultideletionPrompt(false)
      }
      ListFavUiEvent.TapDeleteMultipleFavs -> {
        deleteSelected()
      }

      ListFavUiEvent.TapMultiDeleteEvent -> {
        fetchFavFromNetwork()
      }

      is ListFavUiEvent.ShowConnectivityMessage -> {
        showMessage(R.string.txt_error_connectivity)
      }
    }
  }

  private fun deleteFavs(model: FavoritoModel) {
    viewModelScope.launch {
      useCases.deleteFavorite(model)
      delay(250)
      dismissWarning()
      showMessage(R.string.txt_fav_deletion_success)
    }
  }

  private fun launchFetchFavorites() {
    fetchJob?.cancel()
    fetchJob =
      useCases
        .fetchFavorites()
        .map { it }
        .onEach { _state.update { state -> state.copy(favs = it) } }
        .launchIn(viewModelScope)
  }

  private fun showWarning(sorteo: FavoritoModel? = null) {
    logcat { "clicked fav" }
    _state.update { state -> state.copy(clickedFav = sorteo) }
  }

  private fun dismissWarning() {
    _state.update { state -> state.copy(clickedFav = null) }
  }

  private fun showMessage(@StringRes messageId: Int) {
    viewModelScope.launch { _sideEffect.emit(ListFavoritesSideEffect.ShowSnackBar(messageId)) }
  }

  fun formatDate(favModel: FavoritoModel) =
    useCases.formatFavoriteCreationDate.invoke(favModel.createdAt)

  private fun markItemAsSelected(fav: FavoritoModel, index: Int) {
    val currentFavsMutable = state.value.favs.toMutableList()
    logcat { "Current favs: $currentFavsMutable" }
    logcat { "Tapped index: $index" }

    currentFavsMutable[index] = fav
    _state.update { state -> state.copy(favs = currentFavsMutable) }
  }

  private fun deleteSelected() {
    viewModelScope.launch {
      val selectedFavs = state.value.favs.filter { it.selected }
      logcat { "Selected favs: $selectedFavs" }
      selectedFavs
        .forEach { useCases.deleteFavorite(it) }
        .also {
          clearSelected()
          _sideEffect.emit(ListFavoritesSideEffect.OnMultiDeleteCompleted)
        }
    }
  }

  private fun clearSelected() {
    val clearedFavs = state.value.favs.onEach { it.selected = false }
    logcat { "Cleared favs: $clearedFavs" }
    _state.update { state -> state.copy(favs = clearedFavs, multiselectEnabled = false) }
  }

  private fun showMultideletionPrompt(show: Boolean = false) {
    _state.update { state -> state.copy(showMultiDeletionPrompt = show) }
  }

  private fun fetchFavFromNetwork() {
    _state.update { state -> state.copy(isLoading = true) }
    viewModelScope.launch {
      delay(1.seconds)
      useCases
        .fetchFavoriteFromNetwork()
        .filter { it.isSuccess }
        .filter { it.getOrNull() != null }
        .collectLatest {
          useCases.addFavorite(it.getOrNull()!!)
          sendEvent(ListFavUiEvent.ClearFlags)
        }
    }
  }
}
