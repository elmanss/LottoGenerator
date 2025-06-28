package me.elmanss.melate.common.di

import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Duration
import javax.inject.Singleton
import me.elmanss.melate.Database
import me.elmanss.melate.common.data.network.api.SorteoApi
import me.elmanss.melate.common.data.repository.FavoritosRepository
import me.elmanss.melate.common.data.repository.FavoritosRepositoryImpl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

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
  fun provideClient(): OkHttpClient {
    return OkHttpClient.Builder()
      .apply {
        connectTimeout(Duration.ofSeconds(15))
        readTimeout(Duration.ofSeconds(30))
        writeTimeout(Duration.ofMinutes(1))
        callTimeout(Duration.ofMinutes(2))
      }
      .build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .client(client)
      .baseUrl(SorteoApi.URL)
      .addConverterFactory(MoshiConverterFactory.create())
      .build()
  }

  @Provides
  @Singleton
  fun provideFavoritosRepository(database: Database): FavoritosRepository =
    FavoritosRepositoryImpl(database.favoritoQueries)
}
