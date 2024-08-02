package me.elmanss.melate.business

import app.cash.sqldelight.Query
import kotlinx.coroutines.flow.Flow
import me.elmanss.melate.data.Favorito
import me.elmanss.melate.models.FavoritoModel

interface FavoritesInteractor {
  fun insertFavorite(favoritoModel: FavoritoModel)

  fun deleteFavorite(id: Long)

  fun selectFavorites(): Flow<Query<Favorito>>
}
