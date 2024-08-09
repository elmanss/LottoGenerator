package me.elmanss.melate.home.data.datasource.remote

import logcat.LogPriority
import logcat.logcat
import java.util.Random
import javax.inject.Inject

class SorteoApiImpl
@Inject
constructor(private val random: Random, private val sorteoRange: IntRange) : SorteoApi {
  override fun fetchSorteos(): List<Int> {
    val mutableRandomDraw = mutableSetOf<Int>()
    val shuffledElements = sorteoRange.shuffled(random).toMutableList()
    logcat { "draw starting" }
    fillSet(shuffledElements, mutableRandomDraw)
    logcat { "draw completed" }
    return mutableRandomDraw.sorted()
  }

  private fun randomElementFromList(origin: List<Int>, randomSeed: Long) =
    origin.random(kotlin.random.Random(randomSeed))

  private fun fillSet(origin: MutableList<Int>, destinationSet: MutableSet<Int>) {
    while (destinationSet.size < 6) {
      val randomElement =
        randomElementFromList(origin, random.nextLong()).also { origin.remove(it) }
      logcat { "retrieved element: $randomElement" }
      if (destinationSet.add(randomElement)) {
        logcat { "element added successfully" }
      } else {
        logcat(LogPriority.WARN) { "element already on set" }
      }
    }
  }
}
