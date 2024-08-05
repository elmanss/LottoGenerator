package me.elmanss.melate.home.data.repository

import me.elmanss.melate.home.data.datasource.remote.SorteoApi
import javax.inject.Inject

class SorteoRepositoryImpl @Inject constructor(private val sorteoApi: SorteoApi) :
  SorteoRepository {
  override fun fetchSorteos(): List<Int> {
    return sorteoApi.fetchSorteos()
  }
}
