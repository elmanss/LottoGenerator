package me.elmanss.melate.favorites.domain.usecase

import me.elmanss.melate.favorites.domain.usecase.impl.AddFavoriteUseCase
import me.elmanss.melate.favorites.domain.usecase.impl.DeleteFavoriteUseCase
import me.elmanss.melate.favorites.domain.usecase.impl.FetchFavoritesUseCase
import me.elmanss.melate.favorites.domain.usecase.impl.FetchSorteoFromNetworkUseCase
import me.elmanss.melate.favorites.domain.usecase.impl.FormatFavCreationDateUseCase
import me.elmanss.melate.favorites.domain.usecase.impl.UpdateSubmittedStatusUseCase
import javax.inject.Inject

data class FavoritesUseCases
@Inject
constructor(
  val addFavorite: AddFavoriteUseCase,
  val deleteFavorite: DeleteFavoriteUseCase,
  val fetchFavorites: FetchFavoritesUseCase,
  val formatFavoriteCreationDate: FormatFavCreationDateUseCase,
  val fetchFavoriteFromNetwork: FetchSorteoFromNetworkUseCase,
  val updateSubmittedStatus: UpdateSubmittedStatusUseCase,
)
