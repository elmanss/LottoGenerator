package me.elmanss.melate.home.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import java.util.Random
import java.util.concurrent.ThreadLocalRandom
import me.elmanss.melate.common.data.repository.FavoritosRepository
import me.elmanss.melate.home.data.datasource.remote.SorteoApi
import me.elmanss.melate.home.data.datasource.remote.SorteoApiImpl
import me.elmanss.melate.home.data.repository.SorteoRepository
import me.elmanss.melate.home.data.repository.SorteoRepositoryImpl
import me.elmanss.melate.home.domain.usecase.FetchSorteos
import me.elmanss.melate.home.domain.usecase.HomeUseCases
import me.elmanss.melate.home.domain.usecase.SaveToFavorites

@Module
@InstallIn(ViewModelComponent::class)
object HomeModule {

  @Provides @ViewModelScoped fun provideSorteoRange(): IntRange = 1..56

  @Provides @ViewModelScoped fun providerRandomGenerator(): Random = ThreadLocalRandom.current()

  @Provides
  @ViewModelScoped
  fun provideApi(randomGenerator: Random, sorteoRange: IntRange): SorteoApi =
    SorteoApiImpl(random = randomGenerator, sorteoRange = sorteoRange)

  @Provides
  @ViewModelScoped
  fun provideSorteoRepository(api: SorteoApi): SorteoRepository = SorteoRepositoryImpl(api)

  @Provides
  @ViewModelScoped
  fun provideHomeUseCases(
    favoritosRepository: FavoritosRepository,
    sorteoRepository: SorteoRepository,
  ): HomeUseCases =
    HomeUseCases(FetchSorteos(sorteoRepository), SaveToFavorites(favoritosRepository))
}
