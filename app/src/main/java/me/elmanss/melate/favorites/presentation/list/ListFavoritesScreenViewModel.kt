package me.elmanss.melate.favorites.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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
import me.elmanss.melate.favorites.domain.model.FavoritoModel
import me.elmanss.melate.favorites.domain.usecase.FavoritesUseCases
import javax.inject.Inject

sealed class ListFavUiEvent {
  data object FetchFavs : ListFavUiEvent()

  data object FetchFavFromNetwork : ListFavUiEvent()

  data class ShowDeleteFavDialog(val fav: FavoritoModel) : ListFavUiEvent()

  data class DeleteFav(val fav: FavoritoModel) : ListFavUiEvent()

  data object HideDeleteFavDialog : ListFavUiEvent()

  data class EnableMultiDelete(val fav: FavoritoModel, val index: Int) : ListFavUiEvent()

  data object DisableMultiDelete : ListFavUiEvent()

  data class SelectFav(val fav: FavoritoModel, val index: Int) : ListFavUiEvent()

  data object ShowMultiDeleteFavDialog : ListFavUiEvent()

  data object HideMultiDeleteFavDialog : ListFavUiEvent()

  data object GoToCreate : ListFavUiEvent()

  data object ClearFlags : ListFavUiEvent()

  data object DeleteMultipleFavs : ListFavUiEvent()

  data object HideSuccessMessage : ListFavUiEvent()
}

@HiltViewModel
class ListFavoritesScreenViewModel @Inject constructor(private val useCases: FavoritesUseCases) :
  ViewModel() {
  private val _state = MutableStateFlow(ListFavoritesScreenState())
  val state =
    _state.asStateFlow().stateIn(viewModelScope, SharingStarted.Eagerly, ListFavoritesScreenState())

  private var fetchJob: Job? = null

  init {
    launchFetchFavorites()
  }

  fun sendEvent(event: ListFavUiEvent) {
    when (event) {
      ListFavUiEvent.ClearFlags -> {
        _state.update { state -> state.clearFlags() }
      }
      is ListFavUiEvent.DeleteFav -> {
        deleteFavs(event.fav)
      }
      ListFavUiEvent.DisableMultiDelete -> {
        clearSelected()
      }
      is ListFavUiEvent.EnableMultiDelete -> {
        markItemAsSelected(event.fav, event.index)
        _state.update { state -> state.copy(multiselectEnabled = true) }
      }
      ListFavUiEvent.FetchFavs -> {
        launchFetchFavorites()
      }
      ListFavUiEvent.GoToCreate -> {
        _state.update { state -> state.copy(favTapped = true) }
      }
      ListFavUiEvent.HideDeleteFavDialog -> {
        showWarning()
      }
      is ListFavUiEvent.SelectFav -> {
        markItemAsSelected(event.fav, event.index)
      }
      is ListFavUiEvent.ShowDeleteFavDialog -> {
        showWarning(event.fav)
      }
      is ListFavUiEvent.ShowMultiDeleteFavDialog -> {
        showMultideletionPrompt(true)
      }
      is ListFavUiEvent.HideMultiDeleteFavDialog -> {
        showMultideletionPrompt(false)
      }
      ListFavUiEvent.DeleteMultipleFavs -> {
        deleteSelected()
      }
      ListFavUiEvent.HideSuccessMessage -> {
        showDeletionMessage(false)
      }

      ListFavUiEvent.FetchFavFromNetwork -> {
        fetchFavFromNetwork()
      }
    }
  }

  private fun deleteFavs(model: FavoritoModel) {
    viewModelScope.launch {
      useCases.deleteFavorite(model)
      delay(250)
      dismissWarning()
      showDeletionMessage(true)
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
    _state.update { state -> state.copy(favToDelete = sorteo) }
  }

  private fun dismissWarning() {
    _state.update { state -> state.copy(favToDelete = null) }
  }

  private fun showDeletionMessage(show: Boolean = false) {
    _state.update { state -> state.copy(showDeletionSuccess = show) }
  }

  fun formatDate(favModel: FavoritoModel) =
    useCases.formatFavoriteCreationDate.invoke(favModel.createdAt)

  private fun markItemAsSelected(fav: FavoritoModel, index: Int) {
    val currentFavsMutable = state.value.favs.toMutableList()
    logcat { "Current favs: ${currentFavsMutable}" }
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
          _state.update { state -> state.copy(multideleteCompleted = true) }
        }
    }
  }

  private fun clearSelected() {
    val clearedFavs = state.value.favs.onEach { it.selected = false }
    logcat { "Cleared favs: ${clearedFavs}" }
    _state.update { state -> state.copy(favs = clearedFavs, multiselectEnabled = false) }
  }

  private fun showMultideletionPrompt(show: Boolean = false) {
    _state.update { state -> state.copy(showMultiDeletionPrompt = show) }
  }

  private fun fetchFavFromNetwork() {
    viewModelScope.launch {
      useCases
        .fetchFavoriteFromNetwork()
        .filter { it.isSuccess }
        .filter { it.getOrNull() != null }
        .collectLatest { useCases.addFavorite(it.getOrNull()!!) }
    }
  }
}
