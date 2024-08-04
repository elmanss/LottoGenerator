package me.elmanss.melate.favorites.domain.usecase

import javax.inject.Inject
import me.elmanss.melate.common.data.repository.FavoritosRepository
import me.elmanss.melate.favorites.domain.model.FavoritoModel

class DeleteFavorite @Inject constructor(private val repository: FavoritosRepository) {
  suspend operator fun invoke(fav: FavoritoModel) {
    repository.deleteFavorito(fav.id)
  }
}
