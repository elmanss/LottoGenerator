package me.elmanss.melate.data

data class Sorteo(val numeros: List<Int>) {
    fun prettyPrint(): String {
        return this.numeros.joinToString(separator = ", ", transform = {it.toString()})
    }
}

data class FavoritoModel(val id: Long, val sorteo: String)