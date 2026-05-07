package me.elmanss.melate.common.data.repository

import app.cash.sqldelight.coroutines.asFlow
import me.elmanss.melate.common.data.local.FavOrigin
import me.elmanss.melate.common.domain.repository.FavoritosRepository
import me.elmanss.melate.data.FavoritoQueries
import javax.inject.Inject

class FavoritosRepositoryImpl @Inject constructor(private val dao: FavoritoQueries) :
  FavoritosRepository {
  override suspend fun createFavoritos(sorteoString: String, origin: FavOrigin, createdAt: Long) {
    dao.insertFav(sorteoString, origin, createdAt, 0)
  }

  override fun selectAllFavoritos() = dao.selectAll().asFlow()

  override suspend fun deleteFavorito(favoritoId: Long) {
    dao.deleteFav(favoritoId)
  }

  override suspend fun updateSubmittedStatus(id: Long, isSubmitted: Boolean) {
    dao.updateSubmittedStatus(if (isSubmitted) 1L else 0L, id)
  }
}
