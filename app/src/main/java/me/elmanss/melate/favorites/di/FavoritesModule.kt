package me.elmanss.melate.favorites.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import me.elmanss.melate.common.data.repository.FavoritosRepository
import me.elmanss.melate.favorites.domain.usecase.AddFavorite
import me.elmanss.melate.favorites.domain.usecase.DeleteFavorite
import me.elmanss.melate.favorites.domain.usecase.FavoritesUseCases
import me.elmanss.melate.favorites.domain.usecase.FetchFavorites
import me.elmanss.melate.favorites.domain.usecase.FormatFavCreationDate
import java.time.format.DateTimeFormatter

@Module
@InstallIn(ViewModelComponent::class)
object FavoritesModule {

  @Provides
  @ViewModelScoped
  fun provideDateFormatter() = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

  @Provides
  @ViewModelScoped
  fun provideUseCases(
    repository: FavoritosRepository,
    formatter: DateTimeFormatter,
  ): FavoritesUseCases =
    FavoritesUseCases(
      AddFavorite(repository),
      DeleteFavorite(repository),
      FetchFavorites(repository),
      FormatFavCreationDate(formatter),
    )
}
