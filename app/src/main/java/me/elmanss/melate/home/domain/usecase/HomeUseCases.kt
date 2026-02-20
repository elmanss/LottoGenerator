package me.elmanss.melate.home.domain.usecase

import me.elmanss.melate.home.domain.usecase.impl.FetchSorteosUseCase
import me.elmanss.melate.home.domain.usecase.impl.GetListIdUseCase
import me.elmanss.melate.home.domain.usecase.impl.SaveToFavoritesUseCase

data class HomeUseCases(
  val fetchSorteos: FetchSorteosUseCase,
  val saveToFavorites: SaveToFavoritesUseCase,
  val getListId: GetListIdUseCase,
)
