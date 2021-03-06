package me.elmanss.melate.extensions

import me.elmanss.melate.data.Favorito
import me.elmanss.melate.models.FavoritoModel

fun Favorito.toModel(): FavoritoModel {
    return FavoritoModel(this.id, this.sorteo)
}

fun List<Favorito>.toModelList(): List<FavoritoModel> {
    return this.map { it.toModel() }
}