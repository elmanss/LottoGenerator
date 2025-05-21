package me.elmanss.melate.favorites.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
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

  fun deleteFavs(model: FavoritoModel) {
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

  fun showWarning(sorteo: FavoritoModel? = null) {
    logcat { "clicked fav" }
    _state.update { state -> state.copy(favToDelete = sorteo) }
  }

  fun dismissWarning() {
    _state.update { state -> state.copy(favToDelete = null) }
  }

  fun showDeletionMessage(show: Boolean = false) {
    _state.update { state -> state.copy(showDeletionSuccess = show) }
  }

  fun clearNotifications() {
    dismissWarning()
    showDeletionMessage()
  }

  fun formatDate(favModel: FavoritoModel) =
    useCases.formatFavoriteCreationDate.invoke(favModel.createdAt)

  fun markItemAsSelected(fav: FavoritoModel, index: Int) {
    val currentFavsMutable = state.value.favs.toMutableList()
    currentFavsMutable[index] = fav
    _state.update { state -> state.copy(favs = currentFavsMutable) }
  }

  fun deleteSelected(onFinished: () -> Unit) {
    viewModelScope.launch {
      val selectedFavs = state.value.favs.filter { it.selected }
      logcat { "Selected favs: $selectedFavs" }
      selectedFavs
        .forEach { useCases.deleteFavorite(it) }
        .also {
          clearSelected()
          onFinished.invoke()
        }
    }
  }

  fun clearSelected() {
    val clearedFavs = state.value.favs.onEach { if (it.selected) it.selected = false }
    logcat { "Cleared favs: ${clearedFavs}" }
    _state.update { state -> state.copy(favs = clearedFavs) }
  }

  fun showMultideletionPrompt(show: Boolean = false) {
    _state.update { state -> state.copy(showMultiDeletionPrompt = show) }
  }
}
