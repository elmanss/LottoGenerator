package me.elmanss.melate.common.util

fun List<String>?.prettyPrint(): String {
  return this?.joinToString(", ") ?: ""
}
