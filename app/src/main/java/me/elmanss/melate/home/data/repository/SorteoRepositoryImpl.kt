package me.elmanss.melate.home.data.repository

import me.elmanss.melate.common.domain.datasource.SorteoDataSource
import javax.inject.Inject

class SorteoRepositoryImpl @Inject constructor(private val localDS: SorteoDataSource) :
  SorteoRepository {
  override suspend fun fetchSorteos(): List<Int> {
    val result = localDS.fetchSorteos()
    return if (result.isSuccess) result.getOrNull() ?: emptyList() else emptyList()
  }
}
