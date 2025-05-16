package me.elmanss.melate.favorites.presentation.create

import androidx.core.text.isDigitsOnly
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
import me.elmanss.melate.common.data.local.FavOrigin
import me.elmanss.melate.common.util.prettyPrint
import me.elmanss.melate.favorites.domain.model.FavoritoModel
import me.elmanss.melate.favorites.domain.usecase.FavoritesUseCases
import java.time.ZonedDateTime
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

  fun deleteDigit() {
    val currentInput = state.value.keyboardInput
    if (currentInput.isEmpty()) {
      val currentNumbers = state.value.numbers
      if (currentNumbers.isNotEmpty()) {
        _state.update { state -> state.copy(numbers = currentNumbers.dropLast(1)) }
      }
    } else {
      if (currentInput.length == 1) {
        _state.update { state -> state.copy(keyboardInput = "") }
      } else {
        _state.update { state -> state.copy(keyboardInput = currentInput.dropLast(1)) }
      }
    }
  }

  fun clearSorteoCompleted() {
    _state.update { state -> state.copy(sorteoCompleted = emptyList()) }
  }

  fun clearCaptureNumber() {
    _state.update { state -> state.copy(keyboardInput = "") }
  }

  fun clearError() {
    _state.update { state -> state.copy(captureError = "") }
  }

  fun clearAfterStorage() {
    _state.update { state ->
      state.copy(
        captureError = "",
        keyboardInput = "",
        numbers = emptyList(),
        sorteoCompleted = emptyList(),
        sorteoStored = true,
      )
    }
  }

  fun moveToNext() {
    val currentInput = state.value.keyboardInput
    val currentNumbers = state.value.numbers
    if (currentNumbers.size == MAX_LEN) {
      // show storage prompt
      _state.update { state -> state.copy(sorteoCompleted = currentNumbers) }
    } else {
      when {
        currentInput.isBlank() ->
          _state.update { state -> state.copy(captureError = "Ingresa un numero.") }
        !currentInput.isDigitsOnly() ->
          _state.update { state -> state.copy(captureError = "Solo se permite ingresar numeros.") }
        currentInput.toInt() > 56 ->
          _state.update { state -> state.copy(captureError = "Solo se permiten numeros hasta 56.") }
        isNumberInSorteo(currentInput) ->
          _state.update { state -> state.copy(captureError = "Numero agregado previamente.") }
        else -> addNumberToSorteo(currentInput)
      }
    }
  }

  fun captureDigit(digit: String) {
    logcat { "Capturing digit: $digit" }
    val numbersSize = state.value.numbers.size
    var currentInput = state.value.keyboardInput
    if (currentInput.length < 2) {
      if (numbersSize < MAX_LEN) {
        currentInput += digit
      } else {
        _state.update { state ->
          state.copy(captureError = "El sorteo esta completo, presiona '>' para guardarlo")
        }
        currentInput = ""
      }
      _state.update { state -> state.copy(keyboardInput = currentInput) }
    }
  }

  fun insertFavorite(sorteo: List<String>, onInserted: () -> Unit) {
    viewModelScope.launch {
      val map = sorteo.map { it.toInt() }.sorted().map { it.toString() }
      val model =
        FavoritoModel(
          0,
          map.prettyPrint(),
          FavOrigin.Manual,
          ZonedDateTime.now().toInstant().toEpochMilli(),
        )
      useCases.addFavorite(model)
      onInserted.invoke()
    }
  }

  fun showMessage(show: Boolean) {
    _state.update { state -> state.copy(sorteoStored = show) }
  }

  private fun addNumberToSorteo(number: String) {
    logcat { "Adding number to sorteo: $number" }
    val currentNumbers = state.value.numbers.toMutableList()
    if (currentNumbers.size in MIN_LEN until MAX_LEN) {
      logcat { "Sorteo not complete, adding $number, to index: ${currentNumbers.size}" }
      currentNumbers.add(number)
      _state.update { state -> state.copy(numbers = currentNumbers, keyboardInput = "") }
    }
  }

  private fun isNumberInSorteo(number: String): Boolean {
    return state.value.numbers.contains(number)
  }
}
