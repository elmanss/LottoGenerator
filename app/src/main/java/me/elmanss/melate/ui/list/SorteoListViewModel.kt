package me.elmanss.melate.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import logcat.logcat
import me.elmanss.melate.Melate
import me.elmanss.melate.business.FavoritesInteractor
import me.elmanss.melate.business.FavoritesInteractorImpl
import me.elmanss.melate.extensions.toFavorito
import me.elmanss.melate.getSorteoNumbers
import me.elmanss.melate.models.SorteoModel

import java.util.concurrent.ThreadLocalRandom

class SorteoListViewModel(app: Application) : AndroidViewModel(app) {
    private val interactor: FavoritesInteractor by lazy { FavoritesInteractorImpl((app as Melate).database.favoritoQueries) }
    private val random = ThreadLocalRandom.current()

    private val mSorteos = MutableLiveData<SorteoModel?>(null)
    val sorteos: LiveData<SorteoModel?>
        get() = mSorteos

    fun setSorteos(value: SorteoModel? = null) {
        mSorteos.value = value
    }

    init {
        fetchSorteos()
    }

    fun fetchSorteos() {
        viewModelScope.launch {
            repeat(30) {
                logcat { "$it times" }
                setSorteos(SorteoModel(getSorteoNumbers(random)))
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