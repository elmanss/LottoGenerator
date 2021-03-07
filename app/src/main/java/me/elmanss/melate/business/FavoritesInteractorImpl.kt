package me.elmanss.melate.business

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import me.elmanss.melate.data.Favorito
import me.elmanss.melate.data.FavoritoQueries
import me.elmanss.melate.models.FavoritoModel

class FavoritesInteractorImpl(private val queries: FavoritoQueries) : FavoritesInteractor {
    override fun insertFavorite(favorito: FavoritoModel) {
        queries.insertFav(favorito.sorteo)
    }

    override fun deleteFavorite(id: Long) {
        queries.deleteFav(id)
    }

    override fun selectFavorites(): Flow<Query<Favorito>> {
        return queries.selectAll().asFlow()
    }
}