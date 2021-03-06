package me.elmanss.melate.mapper

import me.elmanss.melate.data.Favorito
import me.elmanss.melate.models.FavoritoModel

object FavoritosMapper {
    private fun entityToModel(fav: Favorito): FavoritoModel {
        return FavoritoModel(fav.id, fav.sorteo)
    }

    fun entitiesToModelList(favs: List<Favorito>): List<FavoritoModel> {
        return favs.map { entityToModel(it) }
    }
}