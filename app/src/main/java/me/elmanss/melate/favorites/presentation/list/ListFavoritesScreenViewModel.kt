package me.elmanss.melate.favorites.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.elmanss.melate.favorites.domain.model.FavoritoModel
import me.elmanss.melate.favorites.domain.usecase.FavoritesUseCases

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
    viewModelScope.launch { useCases.deleteFavorite(model) }
  }

  private fun launchFetchFavorites() {
    fetchJob?.cancel()
    fetchJob =
      useCases
        .fetchFavorites()
        .map { it.map { FavoritoModel(it.id, it.sorteo) } }
        .onEach { _state.update { state -> state.copy(favs = it) } }
        .launchIn(viewModelScope)
  }
}
