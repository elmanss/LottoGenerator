package me.elmanss.melate.favorites.presentation.create

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.logcat
import me.elmanss.melate.R
import me.elmanss.melate.common.util.delegate.viewBinding
import me.elmanss.melate.common.util.prettyPrint
import me.elmanss.melate.databinding.FragmentAddToFavBinding

@AndroidEntryPoint
class CreateFavoriteScreenFragment : Fragment(R.layout.fragment_add_to_fav) {
  companion object {
    private const val ONE = "1"
    private const val TWO = "2"
    private const val THREE = "3"
    private const val FOUR = "4"
    private const val FIVE = "5"
    private const val SIX = "6"
    private const val SEVEN = "7"
    private const val EIGHT = "8"
    private const val NINE = "9"
    private const val ZERO = "0"
  }

  //
  private val binding: FragmentAddToFavBinding by viewBinding()
  private val viewModel: CreateFavoriteScreenViewModel by
    viewModels<CreateFavoriteScreenViewModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    configKeyboard()
    observe()
  }

  private fun setKeyboardEnabled(enabled: Boolean) {
    binding.bKeyboardOne.isEnabled = enabled
    binding.bKeyboardTwo.isEnabled = enabled
    binding.bKeyboardThree.isEnabled = enabled
    binding.bKeyboardFour.isEnabled = enabled
    binding.bKeyboardFive.isEnabled = enabled
    binding.bKeyboardSix.isEnabled = enabled
    binding.bKeyboardSeven.isEnabled = enabled
    binding.bKeyboardEight.isEnabled = enabled
    binding.bKeyboardNine.isEnabled = enabled
  }

  private fun configKeyboard() {
    binding.bKeyboardOne.setOnClickListener {
      viewModel.sendEvent(CreateFavUiEvent.TapDigit((ONE)))
    }

    binding.bKeyboardTwo.setOnClickListener {
      viewModel.sendEvent(CreateFavUiEvent.TapDigit((TWO)))
    }

    binding.bKeyboardThree.setOnClickListener {
      viewModel.sendEvent(CreateFavUiEvent.TapDigit((THREE)))
    }

    binding.bKeyboardFour.setOnClickListener {
      viewModel.sendEvent(CreateFavUiEvent.TapDigit((FOUR)))
    }

    binding.bKeyboardFive.setOnClickListener {
      viewModel.sendEvent(CreateFavUiEvent.TapDigit((FIVE)))
    }

    binding.bKeyboardSix.setOnClickListener {
      viewModel.sendEvent(CreateFavUiEvent.TapDigit((SIX)))
    }

    binding.bKeyboardSeven.setOnClickListener {
      viewModel.sendEvent(CreateFavUiEvent.TapDigit((SEVEN)))
    }

    binding.bKeyboardEight.setOnClickListener {
      viewModel.sendEvent(CreateFavUiEvent.TapDigit((EIGHT)))
    }

    binding.bKeyboardNine.setOnClickListener {
      viewModel.sendEvent(CreateFavUiEvent.TapDigit((NINE)))
    }

    binding.bKeyboardBack.setOnClickListener { viewModel.sendEvent(CreateFavUiEvent.TapDelete) }

    binding.bKeyboardZero.setOnClickListener {
      viewModel.sendEvent(CreateFavUiEvent.TapDigit((ZERO)))
    }

    binding.bKeyboardNext.setOnClickListener { viewModel.sendEvent(CreateFavUiEvent.TapNext) }

    binding.bKeyboardZero.isEnabled = false
  }

  private fun observe() {
    lifecycleScope.launch {
      viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collectLatest {
        if (it.numbers.isNotEmpty()) {
          logcat { "Sorteo not complete, state: $it" }
          setKeyboardEnabled(true)
          binding.tvCaptureNumber.text = ""
          binding.tvKeyboardInfo.text = it.numbers.prettyPrint()
        }

        if (it.sorteoCompleted.isNotEmpty()) {
          logcat { "Sorteo complete, notified sorteo: $it" }
          showSaveDialog(it.sorteoCompleted)
          viewModel.sendEvent(CreateFavUiEvent.ClearEvent(Clearable.SORTEO_COMPLETED))
        }

        if (it.keyboardInput.isNotEmpty()) {
          logcat { "Captured digit: $it" }
          setKeyboardEnabled(it.keyboardInput.length < 2)
          binding.bKeyboardZero.isEnabled = (it.keyboardInput.length == 1)
          binding.tvCaptureNumber.text = it.keyboardInput
          viewModel.sendEvent(CreateFavUiEvent.ClearEvent(Clearable.CAPTURE_NUMBER))
        }

        if (it.captureError.isNotEmpty()) {
          logcat { "Error thrown while capturing digit" }
          Toast.makeText(context, it.captureError, Toast.LENGTH_SHORT).show()
          viewModel.sendEvent(CreateFavUiEvent.ClearEvent(Clearable.ERROR))
        }

        if (it.sorteoInserted) {
          logcat { "Favorito agregado con exito" }
          binding.root.findNavController().navigateUp()
          viewModel.sendEvent(CreateFavUiEvent.ClearEvent(Clearable.AFTER_STORAGE))
        }
      }
    }
  }

  //
  private fun showSaveDialog(sorteo: List<String>) {
    context?.let { c ->
      AlertDialog.Builder(c)
        .setTitle("Números seleccionados")
        .setMessage(
          "Los números que seleccionaste son: \n${sorteo.prettyPrint()}.\n\n ¿Deseas guardarlos?"
        )
        .setPositiveButton("Guardar") { _, _ -> saveToFavs(sorteo) }
        .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
        .show()
    }
  }

  //
  private fun saveToFavs(sorteo: List<String>) {
    viewModel.sendEvent(CreateFavUiEvent.InsertFavorite(sorteo))
  }
}
