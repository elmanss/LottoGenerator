package me.elmanss.melate.home.domain.usecase

import javax.inject.Inject
import me.elmanss.melate.common.data.repository.FavoritosRepository
import me.elmanss.melate.home.domain.model.SorteoModel

class SaveToFavorites @Inject constructor(private val repository: FavoritosRepository) {
  suspend operator fun invoke(sorteo: SorteoModel) {
    repository.createFavoritos(sorteo.numeros.toString())
  }
}
