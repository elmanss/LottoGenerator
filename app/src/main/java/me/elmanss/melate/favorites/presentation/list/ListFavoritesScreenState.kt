package me.elmanss.melate.favorites.presentation.list

import me.elmanss.melate.favorites.domain.model.FavoritoModel

data class ListFavoritesScreenState(
  val favs: List<FavoritoModel> = emptyList(),
  val showDeletionSuccess: Boolean = false,
  val favToDelete: FavoritoModel? = null,
)
