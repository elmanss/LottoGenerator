package me.elmanss.melate.common.data.repository

import app.cash.sqldelight.Query
import kotlinx.coroutines.flow.Flow
import me.elmanss.melate.data.Favorito

interface FavoritosRepository {

  suspend fun createFavoritos(sorteoString: String)

  fun selectAllFavoritos(): Flow<Query<Favorito>>

  suspend fun deleteFavorito(favoritoId: Long)
}
