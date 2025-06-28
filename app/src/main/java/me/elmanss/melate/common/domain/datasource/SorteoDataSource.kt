package me.elmanss.melate.common.domain.datasource

interface SorteoDataSource {
  suspend fun fetchSorteos(): Result<List<Int>>
}