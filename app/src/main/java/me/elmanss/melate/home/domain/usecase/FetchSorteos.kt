package me.elmanss.melate.home.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import me.elmanss.melate.home.data.repository.SorteoRepository
import me.elmanss.melate.home.domain.model.SorteoModel

class FetchSorteos @Inject constructor(private val repository: SorteoRepository) {
  operator fun invoke(): Flow<List<SorteoModel>> {
    val sorteos = mutableListOf<List<Int>>()
    repeat(30) { sorteos.add(repository.fetchSorteos()) }

    return flowOf(sorteos).map { it.map { SorteoModel(it) } }
  }
}
