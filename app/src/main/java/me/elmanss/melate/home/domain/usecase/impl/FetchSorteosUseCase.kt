package me.elmanss.melate.home.domain.usecase.impl

import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.elmanss.melate.home.data.repository.SorteoRepository
import me.elmanss.melate.home.domain.model.SorteoModel

class FetchSorteosUseCase @Inject constructor(private val repository: SorteoRepository) {
  suspend operator fun invoke(): Flow<List<SorteoModel>> {

    val sorteos = coroutineScope {
      val deferredSorteos = List(30) { async { repository.fetchSorteos().getOrNull() } }

      val results = deferredSorteos.awaitAll()

      results.filterNotNull()
    }

    return flowOf(sorteos.map { SorteoModel(it) })
  }
}
