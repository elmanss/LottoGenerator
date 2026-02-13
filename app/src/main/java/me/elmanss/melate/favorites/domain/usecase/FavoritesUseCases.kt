package me.elmanss.melate.favorites.domain.usecase

import me.elmanss.melate.favorites.domain.usecase.impl.AddFavoriteUseCase
import me.elmanss.melate.favorites.domain.usecase.impl.DeleteFavoriteUseCase
import me.elmanss.melate.favorites.domain.usecase.impl.FetchFavoritesUseCase
import me.elmanss.melate.favorites.domain.usecase.impl.FetchSorteoFromNetworkUseCase
import me.elmanss.melate.favorites.domain.usecase.impl.FormatFavCreationDateUseCase

data class FavoritesUseCases(
  val addFavorite: AddFavoriteUseCase,
  val deleteFavorite: DeleteFavoriteUseCase,
  val fetchFavorites: FetchFavoritesUseCase,
  val formatFavoriteCreationDate: FormatFavCreationDateUseCase,
  val fetchFavoriteFromNetwork: FetchSorteoFromNetworkUseCase,
)
