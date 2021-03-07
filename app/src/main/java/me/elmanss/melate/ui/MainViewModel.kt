package me.elmanss.melate.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.elmanss.melate.Melate
import me.elmanss.melate.Pool
import me.elmanss.melate.business.FavoritesInteractor
import me.elmanss.melate.business.FavoritesInteractorImpl
import me.elmanss.melate.extensions.toFavorito
import me.elmanss.melate.models.SorteoModel
import timber.log.Timber
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val interactor: FavoritesInteractor by lazy { FavoritesInteractorImpl((app as Melate).database.favoritoQueries) }
    private val random = ThreadLocalRandom.current()

    private val mSorteos = MutableLiveData<SorteoModel?>(null)
    val sorteos: LiveData<SorteoModel?>
        get() = mSorteos

    fun resetSorteos() {
        mSorteos.value = null
    }

    init {
        fetchSorteos(Pool.numbers)
    }

    private fun shufflePool(pool: List<Int>): List<Int> {
        return pool.shuffled(Random.apply { random })
    }

    private fun getRangeFromPool(pool: List<Int>): List<Int> {
        return shufflePool(pool).subList(0, 6).sorted()
    }

    fun fetchSorteos(pool: List<Int>) {
        viewModelScope.launch {
            repeat(30) {
                Timber.d("$it times")
                mSorteos.value = SorteoModel(getRangeFromPool(pool))
                delay(5)
            }
        }
    }

    fun saveToFavorites(sorteo: SorteoModel) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.insertFavorite(sorteo.toFavorito())
        }
    }
}