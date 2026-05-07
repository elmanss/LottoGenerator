package me.elmanss.melate.favorites.domain.usecase.impl

import kotlinx.coroutines.flow.map
import logcat.LogPriority
import logcat.logcat
import me.elmanss.melate.common.data.local.FavOrigin
import me.elmanss.melate.common.domain.repository.FavoritosRepository
import me.elmanss.melate.favorites.domain.model.FavoritoModel
import javax.inject.Inject

class FetchFavoritesUseCase @Inject constructor(private val repository: FavoritosRepository) {
  operator fun invoke() =
    repository
      .selectAllFavoritos()
      .map { it.executeAsList() }
      .map {
        it.map {
          FavoritoModel(
            id = it.id,
            sorteo = it.sorteo,
            origin = it.origin,
            createdAt = it.created_at,
          )
        }
      }

  private fun mapOrigin(origin: String): FavOrigin {
    return try {
      FavOrigin.valueOf(origin)
    } catch (_: IllegalArgumentException) {
      logcat(LogPriority.WARN) { "Unknown favorite origin in database: '$origin'" }
      FavOrigin.Unknown
    }
  }
}
