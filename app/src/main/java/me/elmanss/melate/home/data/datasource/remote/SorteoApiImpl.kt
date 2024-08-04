package me.elmanss.melate.home.data.datasource.remote

import java.util.concurrent.ThreadLocalRandom
import javax.inject.Inject

class SorteoApiImpl
@Inject
constructor(private val random: ThreadLocalRandom, private val sorteoRange: IntRange) : SorteoApi {
  override fun fetchSorteos(): List<Int> {
    return sorteoRange.shuffled(random).takeLast(6).sorted()
  }
}
