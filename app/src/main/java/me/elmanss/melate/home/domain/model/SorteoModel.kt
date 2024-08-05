package me.elmanss.melate.home.domain.model

data class SorteoModel(val numeros: List<Int>) {
  fun prettyPrint(): String {
    return this.numeros.joinToString(separator = ", ", transform = { it.toString() })
  }
}
