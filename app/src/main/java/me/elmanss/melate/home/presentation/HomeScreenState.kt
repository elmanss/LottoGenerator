package me.elmanss.melate.home.presentation

import me.elmanss.melate.home.domain.model.SorteoModel

data class HomeScreenState(
  val sorteos: List<SorteoModel> = emptyList(),
  val isWarningShown: Boolean = false,
)
