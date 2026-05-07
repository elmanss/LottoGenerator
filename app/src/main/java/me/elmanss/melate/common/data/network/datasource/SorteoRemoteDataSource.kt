package me.elmanss.melate.common.data.network.datasource

import logcat.logcat
import me.elmanss.melate.common.data.network.api.SorteoApi
import me.elmanss.melate.common.domain.datasource.SorteoDataSource
import javax.inject.Inject

class SorteoRemoteDataSource @Inject constructor(private val api: SorteoApi) : SorteoDataSource {

  override suspend fun fetchSorteos(): Result<List<Int>> {
    val response =
      api.fetchSorteos(version = API_VERSION, min = MIN_DRAW, max = MAX_DRAW, count = DRAW_COUNT)
    if (response.isSuccessful) {
      val result = response.body()
      logcat(TAG) { result?.joinToString(separator = "\n") ?: "" }
      return if (result == null) {
        Result.failure(exception = NullPointerException("Draw not found."))
      } else {
        Result.success(result)
      }
    } else {
      return Result.failure(exception = Exception(response.errorBody()?.string() ?: "Error"))
    }
  }

  companion object {
    private const val TAG = "SorteoRemoteDataSource"
    private const val API_VERSION = "v1.0"
    private const val MIN_DRAW = "1"
    private const val MAX_DRAW = "56"
    private const val DRAW_COUNT = "6"
  }
}
