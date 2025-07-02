package me.elmanss.melate.home.data.repository

import javax.inject.Inject
import me.elmanss.melate.common.domain.datasource.SorteoDataSource

class SorteoRepositoryImpl @Inject constructor(private val dataSource: SorteoDataSource) :
  SorteoRepository {
  override suspend fun fetchSorteos(): Result<List<Int>> {
    val result = dataSource.fetchSorteos()
    return result
  }
}
