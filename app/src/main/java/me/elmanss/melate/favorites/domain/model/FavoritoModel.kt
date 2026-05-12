package me.elmanss.melate.favorites.domain.model

import me.elmanss.melate.common.data.local.FavOrigin

data class FavoritoModel(
  val id: Long,
  val sorteo: String,
  val origin: FavOrigin,
  val createdAt: Long = 0,
  val isSubmitted: Boolean = false,
  var selected: Boolean = false,
)
