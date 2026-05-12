package me.elmanss.melate.favorites.domain.usecase.impl

import kotlinx.coroutines.flow.map
import me.elmanss.melate.common.domain.repository.FavoritosRepository
import me.elmanss.melate.favorites.domain.model.FavoritoModel
import javax.inject.Inject

class FetchFavoritesUseCase @Inject constructor(private val repository: FavoritosRepository) {
  operator fun invoke() =
    repository
      .selectAllFavoritos()
      .map { it.executeAsList() }
      .map {
        it.map { row ->
          FavoritoModel(
            id = row.id,
            sorteo = row.sorteo,
            origin = row.origin,
            createdAt = row.created_at,
            isSubmitted = row.is_submitted > 0, // SQLite: 1 = true, 0 = false
          )
        }
      }
}
