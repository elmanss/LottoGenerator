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
import javax.inject.Inject

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
    fetchJob = viewModelScope.launch { fetchSorteos() }
  }

  fun getListId() = useCases.getListId()

  suspend fun fetchSorteos() {
    useCases.fetchSorteos().collectLatest {
      logcat("HomeScreenVm") { it.toString() }
      _state.update { s -> s.copy(sorteos = it) }
    }
  }

  fun launchSaveToFavorites(sorteoModel: SorteoModel) {
    viewModelScope.launch {
      useCases.saveToFavorites(sorteoModel)
      delay(250)
      dismissWarning()
      showSuccessMsg(true)
    }
  }

  fun showWarning(sorteo: SorteoModel? = null) {
    logcat { "clicked sorteo" }
    _state.update { state -> state.copy(isWarningShown = true, clickedSorteo = sorteo) }
  }

  fun dismissWarning() {
    _state.update { state -> state.copy(isWarningShown = false, clickedSorteo = null) }
  }

  fun showSuccessMsg(show: Boolean) {
    _state.update { state -> state.copy(showStorageSuccess = show) }
  }
}
