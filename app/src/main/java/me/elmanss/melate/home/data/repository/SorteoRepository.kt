package me.elmanss.melate.home.data.repository

interface SorteoRepository {

  fun fetchSorteos(): List<Int>
}
