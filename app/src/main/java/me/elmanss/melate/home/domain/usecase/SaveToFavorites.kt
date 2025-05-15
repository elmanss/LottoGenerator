package me.elmanss.melate.home.domain.usecase

import me.elmanss.melate.common.data.local.FavOrigin
import me.elmanss.melate.common.data.repository.FavoritosRepository
import me.elmanss.melate.home.domain.model.SorteoModel
import javax.inject.Inject

class SaveToFavorites @Inject constructor(private val repository: FavoritosRepository) {
  suspend operator fun invoke(sorteo: SorteoModel) {
    repository.createFavoritos(sorteo.numeros.toString(), FavOrigin.Random)
  }
}
