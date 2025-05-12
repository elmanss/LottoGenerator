package me.elmanss.melate.favorites.presentation.create

data class CreateFavoriteScreenState(
    val captureNumber: String = "",
    val captureError: String = "",
    val numbers: List<String> = emptyList(),
    val numberAdded: List<String> = emptyList(),
    val sorteoCompleted: List<String> = emptyList(),
    val numberRemoved: List<String> = emptyList(),
)
