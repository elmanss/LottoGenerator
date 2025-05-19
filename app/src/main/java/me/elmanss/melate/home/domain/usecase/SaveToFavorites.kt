package me.elmanss.melate.home.domain.usecase

import logcat.logcat
import me.elmanss.melate.common.data.local.FavOrigin
import me.elmanss.melate.common.data.repository.FavoritosRepository
import me.elmanss.melate.home.domain.model.SorteoModel
import javax.inject.Inject

class SaveToFavorites @Inject constructor(private val repository: FavoritosRepository) {
  suspend operator fun invoke(sorteo: SorteoModel, createdAt: Long) {
    val storable = sorteo.prettyPrint()
    logcat { "Saving $storable to database" }
    repository.createFavoritos(storable, FavOrigin.Random, createdAt)
  }
}
