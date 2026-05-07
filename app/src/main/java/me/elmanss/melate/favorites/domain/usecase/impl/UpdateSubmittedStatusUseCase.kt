package me.elmanss.melate.favorites.domain.usecase.impl

import me.elmanss.melate.common.domain.repository.FavoritosRepository
import javax.inject.Inject

class UpdateSubmittedStatusUseCase @Inject constructor(private val repository: FavoritosRepository) {
  suspend operator fun invoke(id: Long, isSubmitted: Boolean) =
    repository.updateSubmittedStatus(id, isSubmitted)
}
