package me.elmanss.melate.favorites.presentation.create

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
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
    private const val ERROR_EMPTY_INPUT = "Ingresa un numero."
    private const val ERROR_ONLY_DIGITS = "Solo se permite ingresar numeros."
    private const val ERROR_INPUT_ABOVE_56 = "Solo se permiten numeros hasta 56."
    private const val ERROR_ALREADY_ADDED = "Numero agregado previamente."
    private const val MSG_COMPLETED_DRAW = "El sorteo esta completo, presiona \u2713 para guardarlo"

    private const val SUCCESS = "OK"
  }

  private val _state = MutableStateFlow(CreateFavoriteScreenState())
  val state =
    _state
      .asStateFlow()
      .stateIn(viewModelScope, SharingStarted.Eagerly, CreateFavoriteScreenState())

  private val _sideEffect: MutableSharedFlow<CreateFavSideEffect?> =
    MutableSharedFlow(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  val sideEffect = _sideEffect.asSharedFlow()

  fun sendEvent(event: CreateFavUiEvent) {
    when (event) {
      CreateFavUiEvent.TapNext -> {
        moveToNext()
      }
      CreateFavUiEvent.TapDelete -> {
        deleteDigit()
      }
      is CreateFavUiEvent.TapDigit -> {
        captureDigit(event.digit)
      }

      is CreateFavUiEvent.InsertFavorite -> {
        insertFavorite(event.sorteo)
      }
      is CreateFavUiEvent.ClearEvent -> clear(event.clearable)
    }
  }

  /**
   * Emits a [CreateFavSideEffect] to the UI layer. This is used to trigger one-time events like
   * navigation or showing a snackbar. The operation is launched in the `viewModelScope` to ensure
   * it's lifecycle-aware.
   *
   * @param sideEffect The side effect to be emitted.
   */
  private fun notifySideEffect(sideEffect: CreateFavSideEffect) {
    viewModelScope.launch { _sideEffect.emit(sideEffect) }
  }

  private fun clear(clearable: Clearable) {
    when (clearable) {
      Clearable.SORTEO_COMPLETED -> clearSorteoCompleted()
      Clearable.CAPTURE_NUMBER -> clearCaptureNumber()
    }
  }

  private fun deleteDigit() {
    val currentInput = state.value.keyboardInput
    if (currentInput.isEmpty()) {
      val currentNumbers = state.value.numbers
      if (currentNumbers.isNotEmpty()) {
        _state.update { state -> state.copy(numbers = currentNumbers.dropLast(1)) }
      } else {
        notifySideEffect(CreateFavSideEffect.NavigateBack)
      }
    } else {
      if (currentInput.length == 1) {
        _state.update { state -> state.copy(keyboardInput = "") }
      } else {
        _state.update { state -> state.copy(keyboardInput = currentInput.dropLast(1)) }
      }
    }
  }

  private fun clearSorteoCompleted() {
    _state.update { state -> state.copy(sorteoCompleted = emptyList()) }
  }

  private fun clearCaptureNumber() {
    _state.update { state -> state.copy(keyboardInput = "") }
  }

  private fun moveToNext() {
    val currentInput = state.value.keyboardInput
    val currentNumbers = state.value.numbers
    if (currentNumbers.size == MAX_LEN) {
      // show storage prompt
      _state.update { state -> state.copy(sorteoCompleted = currentNumbers) }
    } else {
      val error = getError(currentInput)
      if (error != SUCCESS) {
        notifySideEffect(CreateFavSideEffect.ShowSnackbar(error, isError = true))
      } else {
        addNumberToSorteo(currentInput)
      }
    }
  }

  private fun getError(currentInput: String): String {
    return when {
      currentInput.isBlank() -> ERROR_EMPTY_INPUT
      !currentInput.isDigitsOnly() -> ERROR_ONLY_DIGITS
      currentInput.toInt() > 56 -> ERROR_INPUT_ABOVE_56
      isNumberInSorteo(currentInput) -> ERROR_ALREADY_ADDED
      else -> SUCCESS
    }
  }

  private fun captureDigit(digit: String) {
    logcat { "Capturing digit: $digit" }
    val numbersSize = state.value.numbers.size
    var currentInput = state.value.keyboardInput
    if (currentInput.length < 2) {
      if (numbersSize < MAX_LEN) {
        currentInput += digit
      } else {
        notifySideEffect(CreateFavSideEffect.ShowSnackbar(MSG_COMPLETED_DRAW, isError = true))
        currentInput = ""
      }
      _state.update { state -> state.copy(keyboardInput = currentInput) }
    }
  }

  private fun insertFavorite(sorteo: List<String>) {
    viewModelScope.launch {
      val sortedSorteoList = sorteo.map { it.toInt() }.sorted().map { it.toString() }
      val model =
        FavoritoModel(
          id = 0,
          sorteo = sortedSorteoList.prettyPrint(),
          origin = FavOrigin.Manual,
          createdAt = ZonedDateTime.now().toInstant().toEpochMilli(),
        )
      useCases.addFavorite(model).also {
        _state.update { state -> state.clear() }
        notifySideEffect(CreateFavSideEffect.ShowSnackbar(SUCCESS, false))
      }
    }
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
