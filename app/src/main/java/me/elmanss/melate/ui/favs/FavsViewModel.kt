package me.elmanss.melate.ui.favs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.elmanss.melate.Melate
import me.elmanss.melate.business.FavoritesInteractor
import me.elmanss.melate.business.FavoritesInteractorImpl
import me.elmanss.melate.extensions.toModelList
import me.elmanss.melate.models.FavoritoModel

class FavsViewModel(app: Application) : AndroidViewModel(app) {
  private val interactor: FavoritesInteractor =
    FavoritesInteractorImpl((app as Melate).database.favoritoQueries)
  private val mFavorites = MutableLiveData<List<FavoritoModel>?>(null)
  val favorites: LiveData<List<FavoritoModel>?>
    get() = mFavorites

  fun resetFavorites() = mFavorites.postValue(null)

  init {
    fetchFavorites()
  }

  fun deleteFavs(model: FavoritoModel) {
    interactor.deleteFavorite(model.id)
  }

  private fun fetchFavorites() {
    viewModelScope.launch {
      interactor.selectFavorites().mapToList(Dispatchers.IO).collect {
        mFavorites.postValue(it.toModelList())
      }
    }
  }
}
