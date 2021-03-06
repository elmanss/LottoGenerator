package me.elmanss.melate.extensions

import me.elmanss.melate.data.Favorito
import me.elmanss.melate.models.FavoritoModel
import me.elmanss.melate.models.SorteoModel

fun Favorito.toModel(): FavoritoModel {
    return FavoritoModel(this.id, this.sorteo)
}

fun List<Favorito>.toModelList(): List<FavoritoModel> {
    return this.map { it.toModel() }
}

fun SorteoModel.toFavorito(): FavoritoModel {
    return FavoritoModel(0L, this.prettyPrint())
}

fun List<String>?.prettyPrint(): String {
    return this?.joinToString(", ") ?: ""
}