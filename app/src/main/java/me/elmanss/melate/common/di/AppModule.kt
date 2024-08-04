package me.elmanss.melate.common.di

import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import me.elmanss.melate.Database
import me.elmanss.melate.common.data.repository.FavoritosRepository
import me.elmanss.melate.common.data.repository.FavoritosRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  @Singleton
  fun provideDriver(app: Application): SqlDriver =
    AndroidSqliteDriver(Database.Schema, app.applicationContext, "favoritos.db")

  @Provides
  @Singleton
  fun provideDatabase(driver: SqlDriver): Database {
    Database.Schema.create(driver)
    return Database(driver)
  }

  @Provides
  @Singleton
  fun provideFavoritosRepository(database: Database): FavoritosRepository =
    FavoritosRepositoryImpl(database.favoritoQueries)
}
