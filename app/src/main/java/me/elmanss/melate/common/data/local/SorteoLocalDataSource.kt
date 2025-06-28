package me.elmanss.melate.common.data.local

import java.util.Random
import javax.inject.Inject
import logcat.LogPriority
import logcat.logcat
import me.elmanss.melate.common.domain.datasource.SorteoDataSource
import me.elmanss.melate.common.util.takeRandom

class SorteoLocalDataSource
@Inject
constructor(private val random: Random, private val sorteoRange: IntRange) : SorteoDataSource {
  override suspend fun fetchSorteos(): Result<List<Int>> {
    val mutableRandomDraw = mutableSetOf<Int>()
    val shuffledElements = sorteoRange.shuffled(random).toMutableList()
    logcat { "draw starting" }
    fillSet(shuffledElements, mutableRandomDraw, random.nextLong())
    logcat { "draw completed" }
    return Result.success(mutableRandomDraw.sorted())
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
