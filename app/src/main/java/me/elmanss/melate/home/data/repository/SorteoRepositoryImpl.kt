package me.elmanss.melate.home.data.repository

import javax.inject.Inject
import me.elmanss.melate.home.data.datasource.remote.SorteoApi

class SorteoRepositoryImpl @Inject constructor(private val sorteoApi: SorteoApi) :
  SorteoRepository {
  override fun fetchSorteos(): List<Int> {
    return sorteoApi.fetchSorteos()
  }
}
