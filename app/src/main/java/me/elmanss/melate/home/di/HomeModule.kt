package me.elmanss.melate.home.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import me.elmanss.melate.common.data.local.SorteoLocalDataSource
import me.elmanss.melate.common.data.repository.FavoritosRepository
import me.elmanss.melate.common.domain.datasource.SorteoDataSource
import me.elmanss.melate.home.data.repository.SorteoRepository
import me.elmanss.melate.home.data.repository.SorteoRepositoryImpl
import me.elmanss.melate.home.domain.usecase.HomeUseCases
import me.elmanss.melate.home.domain.usecase.impl.FetchSorteosUseCase
import me.elmanss.melate.home.domain.usecase.impl.GetListIdUseCase
import me.elmanss.melate.home.domain.usecase.impl.SaveToFavoritesUseCase
import java.util.Random
import java.util.concurrent.ThreadLocalRandom

@Module
@InstallIn(ViewModelComponent::class)
object HomeModule {

  @Provides @ViewModelScoped fun provideSorteoRange(): IntRange = 1..56

  @Provides @ViewModelScoped fun providerRandomGenerator(): Random = ThreadLocalRandom.current()

  @Provides
  @ViewModelScoped
  fun provideApi(randomGenerator: Random, sorteoRange: IntRange): SorteoDataSource =
    SorteoLocalDataSource(random = randomGenerator, sorteoRange = sorteoRange)

  @Provides
  @ViewModelScoped
  fun provideSorteoRepository(api: SorteoDataSource): SorteoRepository = SorteoRepositoryImpl(api)

  @Provides
  @ViewModelScoped
  fun provideHomeUseCases(
    favoritosRepository: FavoritosRepository,
    sorteoRepository: SorteoRepository,
    randomGenerator: Random,
  ): HomeUseCases =
    HomeUseCases(
      FetchSorteosUseCase(sorteoRepository),
      SaveToFavoritesUseCase(favoritosRepository),
      GetListIdUseCase(randomGenerator),
    )
}
