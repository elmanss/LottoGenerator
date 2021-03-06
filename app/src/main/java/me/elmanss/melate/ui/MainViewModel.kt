package me.elmanss.melate.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.elmanss.melate.Melate
import me.elmanss.melate.business.FavoritesInteractor
import me.elmanss.melate.business.FavoritesInteractorImpl
import me.elmanss.melate.data.Sorteo
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val numberPool = listOf(
        1,
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9,
        10,
        11,
        12,
        13,
        14,
        15,
        16,
        17,
        18,
        19,
        20,
        21,
        22,
        23,
        24,
        25,
        26,
        27,
        28,
        29,
        30,
        31,
        32,
        33,
        34,
        35,
        36,
        37,
        38,
        39,
        40,
        41,
        42,
        43,
        44,
        45,
        46,
        47,
        48,
        49,
        50,
        51,
        52,
        53,
        54,
        55,
        56
    )
    private val interactor: FavoritesInteractor by lazy { FavoritesInteractorImpl((app as Melate).database.favoritoQueries) }
    private val random = ThreadLocalRandom.current()

    private val mSorteos = MutableLiveData<List<Sorteo>?>(null)
    val sorteos: LiveData<List<Sorteo>?>
        get() = mSorteos

    fun resetSorteos() = mSorteos.postValue(null)

    private fun shufflePool(): List<Int> {
        return numberPool.shuffled(Random.apply { random })
    }

    private fun getRangeFromPool(): List<Int> {
        return shufflePool().subList(0, 6).sorted()
    }

    fun multiSorteo() {
        val sorteos = mutableListOf<Sorteo>()
        for (i in 0 until 30) {
            sorteos.add(Sorteo(getRangeFromPool()))
        }
        mSorteos.postValue(sorteos)
    }

    fun saveToFavorites(sorteo: Sorteo) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.insertFavorite(sorteo)
        }
    }
}