package me.elmanss.melate.favorites.presentation.create

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
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
    binding.bKeyboardOne.setOnClickListener { viewModel.captureDigit(ONE) }

    binding.bKeyboardTwo.setOnClickListener { viewModel.captureDigit(TWO) }

    binding.bKeyboardThree.setOnClickListener { viewModel.captureDigit(THREE) }

    binding.bKeyboardFour.setOnClickListener { viewModel.captureDigit(FOUR) }

    binding.bKeyboardFive.setOnClickListener { viewModel.captureDigit(FIVE) }

    binding.bKeyboardSix.setOnClickListener { viewModel.captureDigit(SIX) }

    binding.bKeyboardSeven.setOnClickListener { viewModel.captureDigit(SEVEN) }

    binding.bKeyboardEight.setOnClickListener { viewModel.captureDigit(EIGHT) }

    binding.bKeyboardNine.setOnClickListener { viewModel.captureDigit(NINE) }

    binding.bKeyboardBack.setOnClickListener { viewModel.deleteDigit() }

    binding.bKeyboardZero.setOnClickListener { viewModel.captureDigit(ZERO) }

    binding.bKeyboardNext.setOnClickListener { viewModel.moveToNext() }

    binding.bKeyboardZero.isEnabled = false
  }

  private fun observe() {
    viewModel.numberAdded.observe(viewLifecycleOwner) {
      it?.let {
        logcat { "Sorteo not complete, state: $it" }
        setKeyboardEnabled(true)
        binding.tvCaptureNumber.text = ""
        binding.tvKeyboardInfo.text = it.prettyPrint()
        viewModel.resetNumberAdded()
      }
    }
    //
    viewModel.numberRemoved.observe(viewLifecycleOwner) {
      it?.let {
        setKeyboardEnabled(true)
        logcat { "Sorteo not complete, state $it" }
        binding.tvKeyboardInfo.text = it.prettyPrint()
        viewModel.resetNumberRemoved()
      }
    }
    //
    viewModel.sorteoCompleted.observe(viewLifecycleOwner) {
      it?.let {
        logcat { "Sorteo complete, notified sorteo: $it" }
        showSaveDialog(it)
        viewModel.resetCorteoCompleted()
      }
    }
    //
    viewModel.captureNumber.observe(viewLifecycleOwner) {
      it?.let {
        logcat { "Captured digit: $it" }
        setKeyboardEnabled(it.length < 2)
        binding.bKeyboardZero.isEnabled = (it.length == 1)
        binding.tvCaptureNumber.text = it
        viewModel.resetCaptureNumber()
      }
    }
    //
    viewModel.captureError.observe(viewLifecycleOwner) {
      it?.let {
        logcat { "Error thrown while capturing digit" }
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        viewModel.resetCaptureError()
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
    viewModel.insertFavorite(sorteo) {
      logcat { "Favorito agregado con exito" }
      Navigation.findNavController(binding.root).navigateUp()
    }
  }
}
