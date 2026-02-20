package me.elmanss.melate.favorites.domain.usecase.impl

import javax.inject.Inject
import me.elmanss.melate.common.domain.repository.FavoritosRepository
import me.elmanss.melate.favorites.domain.model.FavoritoModel

class AddFavoriteUseCase @Inject constructor(private val repository: FavoritosRepository) {
  suspend operator fun invoke(model: FavoritoModel) {
    repository.createFavoritos(model.sorteo, model.origin, model.createdAt)
  }
}
