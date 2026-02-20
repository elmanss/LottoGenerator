package me.elmanss.melate.favorites.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.elmanss.melate.common.data.local.FavOrigin
import me.elmanss.melate.favorites.domain.model.FavoritoModel
import me.elmanss.melate.home.data.repository.SorteoRepository
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class FetchSorteoFromNetworkUseCase
@Inject
constructor(private val sorteoRepository: SorteoRepository) {
  @OptIn(ExperimentalTime::class)
  suspend operator fun invoke(): Flow<Result<FavoritoModel>> {

    val result = sorteoRepository.fetchSorteos()

    return if (result.isSuccess && result.getOrNull() != null) {
      flowOf(Result.success(result.getOrNull()!!.sorted().toFavorito()))
    } else {
      flowOf(Result.failure(result.exceptionOrNull() ?: Throwable()))
    }
  }

  @OptIn(ExperimentalTime::class)
  private fun List<Int>.toFavorito() =
    FavoritoModel(
      id = 0L,
      sorteo = this.joinToString(),
      origin = FavOrigin.Network,
      createdAt = Clock.System.now().toEpochMilliseconds(),
      selected = false,
    )
}
