package me.elmanss.melate.common.data.network.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SorteoApi {
  companion object {
    const val URL = "https://www.randomnumberapi.com/api/"
  }

  @GET("{version}/random")
  suspend fun fetchSorteos(
    @Path("version") version: String,
    @Query("min") min: String,
    @Query("max") max: String,
    @Query("count") count: String,
  ): Response<List<Int>>
}
