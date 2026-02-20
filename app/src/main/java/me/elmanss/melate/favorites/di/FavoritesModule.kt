package me.elmanss.melate.favorites.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import java.time.format.DateTimeFormatter
import javax.inject.Named
import me.elmanss.melate.common.data.network.api.SorteoApi
import me.elmanss.melate.common.data.network.datasource.SorteoRemoteDataSource
import me.elmanss.melate.common.domain.datasource.SorteoDataSource
import me.elmanss.melate.common.domain.repository.FavoritosRepository
import me.elmanss.melate.favorites.domain.usecase.FavoritesUseCases
import me.elmanss.melate.favorites.domain.usecase.impl.AddFavoriteUseCase
import me.elmanss.melate.favorites.domain.usecase.impl.DeleteFavoriteUseCase
import me.elmanss.melate.favorites.domain.usecase.impl.FetchFavoritesUseCase
import me.elmanss.melate.favorites.domain.usecase.impl.FetchSorteoFromNetworkUseCase
import me.elmanss.melate.favorites.domain.usecase.impl.FormatFavCreationDateUseCase
import me.elmanss.melate.home.data.repository.SorteoRepository
import me.elmanss.melate.home.data.repository.SorteoRepositoryImpl
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
object FavoritesModule {

  @Provides
  @ViewModelScoped
  fun provideDateFormatter(): DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

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
      addFavorite = AddFavoriteUseCase(repository),
      deleteFavorite = DeleteFavoriteUseCase(repository),
      fetchFavorites = FetchFavoritesUseCase(repository),
      formatFavoriteCreationDate = FormatFavCreationDateUseCase(formatter),
      fetchFavoriteFromNetwork = FetchSorteoFromNetworkUseCase(sorteoRepo),
    )
}
