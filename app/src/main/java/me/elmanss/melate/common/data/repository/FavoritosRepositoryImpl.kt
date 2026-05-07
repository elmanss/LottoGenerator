package me.elmanss.melate.common.data.repository

import app.cash.sqldelight.coroutines.asFlow
import me.elmanss.melate.common.data.local.FavOrigin
import me.elmanss.melate.common.domain.repository.FavoritosRepository
import me.elmanss.melate.data.FavoritoQueries
import javax.inject.Inject

class FavoritosRepositoryImpl @Inject constructor(private val dao: FavoritoQueries) :
  FavoritosRepository {
  override suspend fun createFavoritos(sorteoString: String, origin: FavOrigin, createdAt: Long) {
    dao.insertFav(sorteoString, origin, createdAt)
  }

  override fun selectAllFavoritos() = dao.selectAll().asFlow()

  override suspend fun deleteFavorito(favoritoId: Long) {
    dao.deleteFav(favoritoId)
  }
}
