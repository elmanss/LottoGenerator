package me.elmanss.melate.favorites.domain.usecase

import me.elmanss.melate.common.data.repository.FavoritosRepository
import me.elmanss.melate.favorites.domain.model.FavoritoModel
import javax.inject.Inject

class AddFavorite @Inject constructor(private val repository: FavoritosRepository) {
  suspend operator fun invoke(model: FavoritoModel) {
    repository.createFavoritos(model.sorteo)
  }
}
