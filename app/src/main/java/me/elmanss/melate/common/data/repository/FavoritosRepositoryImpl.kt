package me.elmanss.melate.common.data.repository

import app.cash.sqldelight.coroutines.asFlow
import javax.inject.Inject
import me.elmanss.melate.data.FavoritoQueries

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
