package me.elmanss.melate.business

import com.squareup.sqldelight.Query
import kotlinx.coroutines.flow.Flow
import me.elmanss.melate.data.Favorito
import me.elmanss.melate.models.SorteoModel

interface FavoritesInteractor {
    fun insertFavorite(sorteo: SorteoModel)
    fun deleteFavorite(id: Long)
    fun selectFavorites() : Flow<Query<Favorito>>
}