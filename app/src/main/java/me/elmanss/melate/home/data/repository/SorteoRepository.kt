package me.elmanss.melate.home.data.repository

interface SorteoRepository {

  suspend fun fetchSorteos(): Result<List<Int>>
}
