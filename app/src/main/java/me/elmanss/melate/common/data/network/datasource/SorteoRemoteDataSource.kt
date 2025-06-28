package me.elmanss.melate.common.data.network.datasource

import logcat.LogPriority
import logcat.logcat
import me.elmanss.melate.common.data.network.api.SorteoApi
import me.elmanss.melate.common.domain.datasource.SorteoDataSource

class SorteoRemoteDataSource(private val api: SorteoApi) : SorteoDataSource {
  override suspend fun fetchSorteos(): Result<List<Int>> {
    val response = api.fetchSorteos("1", "1", "56", "6")
    if (response.isSuccessful) {
      return Result.success(response.body().orEmpty())
    } else {
      logcat(priority = LogPriority.ERROR) { response.errorBody()?.string() ?: "Error" }
      return Result.failure(exception = Exception(response.errorBody()?.string() ?: "Error"))
    }
  }
}
