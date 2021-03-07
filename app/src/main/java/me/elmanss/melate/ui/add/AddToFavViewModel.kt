package me.elmanss.melate.ui.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import me.elmanss.melate.Melate
import me.elmanss.melate.business.FavoritesInteractor
import me.elmanss.melate.business.FavoritesInteractorImpl
import me.elmanss.melate.models.FavoritoModel
import timber.log.Timber

class AddToFavViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        const val MIN_LEN = 0
        const val MAX_LEN = 6
    }

    private val interactor: FavoritesInteractor =
        FavoritesInteractorImpl((app as Melate).database.favoritoQueries)

    var currentNumber = ""
    private val mCaptureNumber = MutableLiveData<String?>(null)
    val captureNumber: LiveData<String?>
        get() = mCaptureNumber

    fun resetCaptureNumber() {
        mCaptureNumber.value = null
    }

    private val mCaptureError = MutableLiveData<String?>(null)
    val captureError: LiveData<String?>
        get() = mCaptureError

    fun resetCaptureError() {
        mCaptureError.value = null
    }

    private val mNumbers = mutableListOf<String>()

    private val mNumberAdded = MutableLiveData<List<String>?>(null)
    val numberAdded: LiveData<List<String>?>
        get() = mNumberAdded

    fun resetNumberAdded() {
        mNumberAdded.value = null
    }

    private val mSorteoCompleted = MutableLiveData<List<String>?>(null)
    val sorteoCompleted: LiveData<List<String>?>
        get() = mSorteoCompleted

    fun resetCorteoCompleted() {
        mSorteoCompleted.value = null
    }

    private val mNumberRemoved = MutableLiveData<List<String>?>(null)
    val numberRemoved: LiveData<List<String>?>
        get() = mNumberRemoved

    fun resetNumberRemoved() {
        mNumberRemoved.value = null
    }

    fun deleteDigit() {
        if (currentNumber.isNotEmpty()) {
            currentNumber = currentNumber.dropLast(1)
        }

        if (currentNumber.isEmpty()) {
            removeNumberFromSorteo()
        }

        mCaptureNumber.value = currentNumber
    }

    fun moveToNext() {
        when {
            currentNumber.isBlank() && mNumbers.size < MAX_LEN -> mCaptureError.value =
                "Ingresa un numero"
            isNumberInSorteo(currentNumber) && mNumbers.size < MAX_LEN -> mCaptureError.value =
                "Numero agregado previamente"
            else -> addNumberToSorteo(currentNumber)
        }
    }

    fun captureDigit(digit: String) {
        Timber.d("Capturing digit: %s", digit)
        if (mNumbers.size < MAX_LEN) {
            currentNumber += digit
        } else {
            mCaptureError.value = "El sorteo esta completo, presiona '>' para guardarlo"
            currentNumber = ""
        }
        mCaptureNumber.value = currentNumber
    }

    fun insertFavorite(favoritoModel: FavoritoModel) {
        interactor.insertFavorite(favoritoModel)
    }

    private fun addNumberToSorteo(number: String) {
        Timber.d("Adding number to sorteo: %s", number)
        if (mNumbers.size in MIN_LEN until MAX_LEN) {
            Timber.d("Sorteo not complete, adding %s, to index: %d", number, mNumbers.size)
            mNumbers.add(number)
            mNumberAdded.value = mNumbers
            currentNumber = ""
        } else if (mNumbers.size == MAX_LEN) {
            Timber.d("Sorteo complete, notifying sorteo: %s", mNumbers)
            mSorteoCompleted.value = mNumbers
        }
    }

    private fun removeNumberFromSorteo() {
        if (mNumbers.isNotEmpty()) {
            mNumbers.removeLast()
            if (mNumbers.isNotEmpty()) {
                val last = mNumbers.last()
                currentNumber = last
            }

            mNumberRemoved.value = mNumbers
        }
    }

    private fun isNumberInSorteo(number: String): Boolean {
        return mNumbers.any { it == number }
    }
}