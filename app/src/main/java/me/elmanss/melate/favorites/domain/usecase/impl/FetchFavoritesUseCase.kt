package me.elmanss.melate.favorites.domain.usecase.impl

import kotlinx.coroutines.flow.map
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
        it.map { FavoritoModel(it.id, it.sorteo, FavOrigin.valueOf(it.origin), it.created_at) }
      }
}
