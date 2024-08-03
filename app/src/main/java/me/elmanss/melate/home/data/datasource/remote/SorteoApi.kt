package me.elmanss.melate.home.data.datasource.remote

interface SorteoApi {
  fun fetchSorteos(): List<Int>
}
