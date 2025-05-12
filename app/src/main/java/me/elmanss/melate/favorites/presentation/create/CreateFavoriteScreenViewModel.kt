package me.elmanss.melate.favorites.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import logcat.logcat
import me.elmanss.melate.common.util.legacyRemoveLast
import me.elmanss.melate.common.util.prettyPrint
import me.elmanss.melate.favorites.domain.model.FavoritoModel
import me.elmanss.melate.favorites.domain.usecase.FavoritesUseCases
import javax.inject.Inject

@HiltViewModel
class CreateFavoriteScreenViewModel @Inject constructor(private val useCases: FavoritesUseCases) :
  ViewModel() {
  companion object {
    const val MIN_LEN = 0
    const val MAX_LEN = 6
  }

  private val _state = MutableStateFlow(CreateFavoriteScreenState())
  val state =
    _state
      .asStateFlow()
      .stateIn(viewModelScope, SharingStarted.Eagerly, CreateFavoriteScreenState())

  var currentNumber = ""

  private val mNumbers = mutableListOf<String>()

  fun deleteDigit() {
    if (currentNumber.isNotEmpty()) {
      currentNumber = currentNumber.dropLast(1)
    }

    if (currentNumber.isEmpty()) {
      removeNumberFromSorteo()
    }

    _state.update { state -> state.copy(captureNumber = currentNumber) }
  }

  fun clearNumberAdded() {
    _state.update { state -> state.copy(numberAdded = emptyList()) }
  }

  fun clearNumberRemoved() {
    _state.update { state -> state.copy(numberRemoved = emptyList()) }
  }

  fun clearSorteoCompleted() {
    _state.update { state -> state.copy(sorteoCompleted = emptyList()) }
  }

  fun clearCaptureNumber() {
    _state.update { state -> state.copy(captureNumber = "") }
  }

  fun clearError() {
    _state.update { state -> state.copy(captureError = "") }
  }

  fun moveToNext() {
    when {
      currentNumber.isBlank() && mNumbers.size < MAX_LEN ->
        _state.update { state -> state.copy(captureError = "Ingresa un numero") }
      currentNumber.toInt() > 56 && mNumbers.size < MAX_LEN ->
        _state.update { state -> state.copy(captureError = "Solo se permiten numeros hasta 56") }
      isNumberInSorteo(currentNumber) && mNumbers.size < MAX_LEN ->
        _state.update { state -> state.copy(captureError = "Numero agregado previamente") }
      else -> addNumberToSorteo(currentNumber)
    }
  }

  fun captureDigit(digit: String) {
    logcat { "Capturing digit: $digit" }
    if (mNumbers.size < MAX_LEN) {
      currentNumber += digit
    } else {
      _state.update { state ->
        state.copy(captureError = "El sorteo esta completo, presiona '>' para guardarlo")
      }
      currentNumber = ""
    }
    _state.update { state -> state.copy(captureNumber = currentNumber) }
  }

  fun insertFavorite(sorteo: List<String>, onInserted: () -> Unit) {
    viewModelScope.launch {
      val map = sorteo.map { it.toInt() }.sorted().map { it.toString() }
      val model = FavoritoModel(0, map.prettyPrint())
      useCases.addFavorite(model)
      onInserted.invoke()
    }
  }

  private fun addNumberToSorteo(number: String) {
    logcat { "Adding number to sorteo: $number" }
    if (mNumbers.size in MIN_LEN until MAX_LEN) {
      logcat { "Sorteo not complete, adding $number, to index: ${mNumbers.size}" }
      mNumbers.add(number)
      _state.update { state -> state.copy(numberAdded = mNumbers) }
      currentNumber = ""
      if (mNumbers.size == MAX_LEN) {
        _state.update { state -> state.copy(sorteoCompleted = mNumbers) }
      }
    } else if (mNumbers.size == MAX_LEN) {
      logcat { "Sorteo complete, notifying sorteo: $mNumbers" }
      _state.update { state -> state.copy(sorteoCompleted = mNumbers) }
    }
  }

  private fun removeNumberFromSorteo() {
    if (mNumbers.isNotEmpty()) {
      currentNumber = mNumbers.legacyRemoveLast()
      _state.update { state -> state.copy(numberRemoved = mNumbers) }
    }
  }

  private fun isNumberInSorteo(number: String): Boolean {
    return mNumbers.any { it == number }
  }
}
