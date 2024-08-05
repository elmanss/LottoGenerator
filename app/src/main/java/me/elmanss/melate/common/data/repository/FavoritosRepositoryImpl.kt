package me.elmanss.melate.common.data.repository

import app.cash.sqldelight.coroutines.asFlow
import me.elmanss.melate.data.FavoritoQueries
import javax.inject.Inject

class FavoritosRepositoryImpl @Inject constructor(private val dao: FavoritoQueries) :
  FavoritosRepository {
  override suspend fun createFavoritos(sorteoString: String) {
    dao.insertFav(sorteoString)
  }

  override fun selectAllFavoritos() = dao.selectAll().asFlow()

  override suspend fun deleteFavorito(favoritoId: Long) {
    dao.deleteFav(favoritoId)
  }
}
