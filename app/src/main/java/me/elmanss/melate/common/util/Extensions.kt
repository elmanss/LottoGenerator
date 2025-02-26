package me.elmanss.melate.common.util

import kotlin.random.Random

fun List<String>?.prettyPrint(): String {
  return this?.joinToString(", ") ?: ""
}

@Throws(NoSuchElementException::class)
fun <T> MutableList<T>.takeRandom(random: Random = Random): T {
  if (isEmpty()) {
    throw NoSuchElementException("List is empty")
  }

  val randomIndex = random.nextInt(size)
  return removeAt(randomIndex)
}

fun <T> MutableList<T>.legacyRemoveLast(): T = this.removeAt(this.lastIndex)
