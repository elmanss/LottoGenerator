package me.elmanss.melate.home.domain.usecase.impl

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import me.elmanss.melate.home.data.repository.SorteoRepository
import me.elmanss.melate.home.domain.model.SorteoModel

class FetchSorteosUseCase @Inject constructor(private val repository: SorteoRepository) {
  suspend operator fun invoke(): Flow<List<SorteoModel>> {
    val sorteos = mutableListOf<List<Int>>()
    repeat(30) {
      val result = repository.fetchSorteos()
      if (result.isSuccess && result.getOrNull() != null) {
        sorteos.add(result.getOrNull()!!)
      }
    }

    return flowOf(sorteos).map { it.map { SorteoModel(it) } }
  }
}
