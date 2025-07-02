package me.elmanss.melate.favorites.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import me.elmanss.melate.common.data.network.api.SorteoApi
import me.elmanss.melate.common.data.network.datasource.SorteoRemoteDataSource
import me.elmanss.melate.common.data.repository.FavoritosRepository
import me.elmanss.melate.common.domain.datasource.SorteoDataSource
import me.elmanss.melate.favorites.domain.usecase.AddFavorite
import me.elmanss.melate.favorites.domain.usecase.DeleteFavorite
import me.elmanss.melate.favorites.domain.usecase.FavoritesUseCases
import me.elmanss.melate.favorites.domain.usecase.FetchSorteoFromNetwork
import me.elmanss.melate.favorites.domain.usecase.FetchFavorites
import me.elmanss.melate.favorites.domain.usecase.FormatFavCreationDate
import me.elmanss.melate.home.data.repository.SorteoRepository
import me.elmanss.melate.home.data.repository.SorteoRepositoryImpl
import retrofit2.Retrofit
import java.time.format.DateTimeFormatter
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object FavoritesModule {

  @Provides
  @ViewModelScoped
  fun provideDateFormatter() = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

  @Provides
  @ViewModelScoped
  @Named("remoteDS")
  fun provideRemoteDataSource(retrofit: Retrofit): SorteoDataSource =
    SorteoRemoteDataSource(retrofit.create(SorteoApi::class.java))

  @Provides
  @ViewModelScoped
  @Named("remoteRepo")
  fun provideSorteoRepository(@Named("remoteDS") dataSource: SorteoDataSource): SorteoRepository =
    SorteoRepositoryImpl(dataSource)

  @Provides
  @ViewModelScoped
  fun provideUseCases(
    repository: FavoritosRepository,
    formatter: DateTimeFormatter,
    @Named("remoteRepo") sorteoRepo: SorteoRepository,
  ): FavoritesUseCases =
    FavoritesUseCases(
      AddFavorite(repository),
      DeleteFavorite(repository),
      FetchFavorites(repository),
      FormatFavCreationDate(formatter),
      FetchSorteoFromNetwork(sorteoRepo),
    )
}
