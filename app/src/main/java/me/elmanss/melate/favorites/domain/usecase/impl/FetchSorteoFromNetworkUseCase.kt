package me.elmanss.melate.favorites.domain.usecase.impl

import me.elmanss.melate.common.data.local.FavOrigin
import me.elmanss.melate.favorites.domain.model.FavoritoModel
import me.elmanss.melate.home.data.repository.SorteoRepository
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class FetchSorteoFromNetworkUseCase
@OptIn(ExperimentalTime::class)
@Inject
constructor(private val sorteoRepository: SorteoRepository, private val clock: Clock) {
  @OptIn(ExperimentalTime::class)
  suspend operator fun invoke(): Result<FavoritoModel> {
    val result = sorteoRepository.fetchSorteos()
    return result.map { it.toFavorito() }
  }

  @OptIn(ExperimentalTime::class)
  private fun List<Int>.toFavorito() =
    FavoritoModel(
      id = 0L,
      sorteo = this.joinToString(),
      origin = FavOrigin.Network,
      createdAt = clock.now().toEpochMilliseconds(),
      selected = false,
    )
}
