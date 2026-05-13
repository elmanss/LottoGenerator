package me.elmanss.melate.home.domain.model

import java.util.UUID

data class SorteoModel(
  val numeros: List<Int>,
  val id: String = UUID.randomUUID().toString(),
  val selected: Boolean = false
) {
  fun prettyPrint(): String {
    return this.numeros.joinToString(separator = ", ", transform = { it.toString() })
  }
}
