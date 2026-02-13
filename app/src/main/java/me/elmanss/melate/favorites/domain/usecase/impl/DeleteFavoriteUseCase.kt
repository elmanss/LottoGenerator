package me.elmanss.melate.favorites.domain.usecase.impl

import me.elmanss.melate.common.data.repository.FavoritosRepository
import me.elmanss.melate.favorites.domain.model.FavoritoModel
import javax.inject.Inject

class DeleteFavoriteUseCase @Inject constructor(private val repository: FavoritosRepository) {
  suspend operator fun invoke(fav: FavoritoModel) {
    repository.deleteFavorito(fav.id)
  }
}
