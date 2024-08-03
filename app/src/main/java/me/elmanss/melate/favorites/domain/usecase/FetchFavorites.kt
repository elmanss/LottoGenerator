package me.elmanss.melate.favorites.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.map
import me.elmanss.melate.common.data.repository.FavoritosRepository
import me.elmanss.melate.favorites.domain.model.FavoritoModel

class FetchFavorites @Inject constructor(private val repository: FavoritosRepository) {
  operator fun invoke() =
    repository
      .selectAllFavoritos()
      .map { it.executeAsList() }
      .map { it.map { FavoritoModel(it.id, it.sorteo) } }
}
