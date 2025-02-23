package me.elmanss.melate.home.data.datasource.remote

import java.util.Random
import javax.inject.Inject
import logcat.LogPriority
import logcat.logcat
import me.elmanss.melate.common.util.takeRandom

class SorteoApiImpl
@Inject
constructor(private val random: Random, private val sorteoRange: IntRange) : SorteoApi {
  override fun fetchSorteos(): List<Int> {
    val mutableRandomDraw = mutableSetOf<Int>()
    val shuffledElements = sorteoRange.shuffled(random).toMutableList()
    logcat { "draw starting" }
    fillSet(shuffledElements, mutableRandomDraw, random.nextLong())
    logcat { "draw completed" }
    return mutableRandomDraw.sorted()
  }

  private fun fillSet(origin: MutableList<Int>, destinationSet: MutableSet<Int>, seed: Long) {
    while (destinationSet.size < 6) {
      if (origin.isNotEmpty()) {
        val randomElement = origin.takeRandom(kotlin.random.Random(seed))
        destinationSet.add(randomElement)
        logcat { "element $randomElement added successfully" }
      } else {
        logcat(priority = LogPriority.WARN) { "draw source is empty" }
        break
      }
    }
  }
}
