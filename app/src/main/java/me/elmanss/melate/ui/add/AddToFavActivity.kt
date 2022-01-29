package me.elmanss.melate.ui.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import logcat.logcat
import me.elmanss.melate.databinding.ActivityAddToFavBinding
import me.elmanss.melate.extensions.prettyPrint
import me.elmanss.melate.models.FavoritoModel


class AddToFavActivity : AppCompatActivity() {
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

        const val REQUEST_CODE = 200
        fun startForResult(context: Context) {
            ((context as Activity).startActivityForResult(
                Intent(
                    context,
                    AddToFavActivity::class.java
                ), REQUEST_CODE
            ))
        }
    }

    private lateinit var binding: ActivityAddToFavBinding
    private val viewModel: AddToFavViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddToFavBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
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
            viewModel.captureDigit(ONE)
        }

        binding.bKeyboardTwo.setOnClickListener {
            viewModel.captureDigit(TWO)
        }

        binding.bKeyboardThree.setOnClickListener {
            viewModel.captureDigit(THREE)
        }

        binding.bKeyboardFour.setOnClickListener {
            viewModel.captureDigit(FOUR)
        }

        binding.bKeyboardFive.setOnClickListener {
            viewModel.captureDigit(FIVE)
        }

        binding.bKeyboardSix.setOnClickListener {
            viewModel.captureDigit(SIX)
        }

        binding.bKeyboardSeven.setOnClickListener {
            viewModel.captureDigit(SEVEN)
        }

        binding.bKeyboardEight.setOnClickListener {
            viewModel.captureDigit(EIGHT)
        }

        binding.bKeyboardNine.setOnClickListener {
            viewModel.captureDigit(NINE)
        }

        binding.bKeyboardBack.setOnClickListener {
            viewModel.deleteDigit()
        }

        binding.bKeyboardZero.setOnClickListener {
            viewModel.captureDigit(ZERO)
        }

        binding.bKeyboardNext.setOnClickListener {
            viewModel.moveToNext()
        }

        binding.bKeyboardZero.isEnabled = false
    }

    private fun observe() {
        viewModel.numberAdded.observe(this) {
            it?.let {
                logcat { "Sorteo not complete, state: $it" }
                setKeyboardEnabled(true)
                binding.tvCaptureNumber.text = ""
                binding.tvKeyboardInfo.text = it.prettyPrint()
                viewModel.resetNumberAdded()
            }
        }

        viewModel.numberRemoved.observe(this) {
            it?.let {
                setKeyboardEnabled(true)
                logcat { "Sorteo not complete, state $it" }
                binding.tvKeyboardInfo.text = it.prettyPrint()
                viewModel.resetNumberRemoved()
            }
        }

        viewModel.sorteoCompleted.observe(this) {
            it?.let {
                logcat { "Sorteo complete, notified sorteo: $it" }
                showSaveDialog(it)
                viewModel.resetCorteoCompleted()
            }
        }

        viewModel.captureNumber.observe(this) {
            it?.let {
                logcat { "Captured digit: $it" }
                setKeyboardEnabled(it.length < 2)
                binding.bKeyboardZero.isEnabled = (it.length == 1)
                binding.tvCaptureNumber.text = it
                viewModel.resetCaptureNumber()
            }
        }

        viewModel.captureError.observe(this) {
            it?.let {
                logcat { "Error thrown while capturing digit" }
                Toast.makeText(this@AddToFavActivity, it, Toast.LENGTH_SHORT).show()
                viewModel.resetCaptureError()
            }
        }
    }

    private fun showSaveDialog(sorteo: List<String>) {
        AlertDialog.Builder(this)
            .setTitle("Números seleccionados")
            .setMessage("Los números que seleccionaste son: \n${sorteo.prettyPrint()}.\n\n ¿Deseas guardarlos?")
            .setPositiveButton("Guardar") { _, _ ->
                saveToFavs(sorteo)
            }
            .setNegativeButton("Cancelar") { d, _ ->
                d.dismiss()
            }.show()
    }

    private fun saveToFavs(sorteo: List<String>) {
        viewModel.insertFavorite(FavoritoModel(0, sorteo.prettyPrint()))
        logcat { "Favorito agregado con exito" }
        setResult(Activity.RESULT_OK)
        finish()
    }
}
