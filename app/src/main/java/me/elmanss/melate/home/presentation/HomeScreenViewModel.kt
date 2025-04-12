package me.elmanss.melate.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
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

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val useCases: HomeUseCases) : ViewModel() {
  private val _state = MutableStateFlow(HomeScreenState())
  val state = _state.asStateFlow().stateIn(viewModelScope, SharingStarted.Lazily, HomeScreenState())
  private var fetchJob: Job? = null

  init {
    launchFetchSorteos()
  }

  fun launchFetchSorteos() {
    fetchJob?.cancel()
    fetchJob =
      viewModelScope.launch {
        useCases.fetchSorteos().collectLatest {
          logcat("HomeScreenVm") { it.toString() }
          _state.update { s -> s.copy(sorteos = it) }
        }
      }
  }

  fun launchSaveToFavorites(sorteoModel: SorteoModel) {
    viewModelScope.launch { useCases.saveToFavorites(sorteoModel) }
  }

  fun showWarning() {
    _state.update { state -> state.copy(isWarningShown = true) }
  }

  fun dismissWarning() {
    _state.update { state -> state.copy(isWarningShown = false) }
  }
}
