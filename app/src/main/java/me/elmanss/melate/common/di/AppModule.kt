package me.elmanss.melate.common.di

import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import logcat.logcat
import me.elmanss.melate.Database
import me.elmanss.melate.common.data.network.api.SorteoApi
import me.elmanss.melate.common.data.repository.FavoritosRepository
import me.elmanss.melate.common.data.repository.FavoritosRepositoryImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import javax.inject.Singleton

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
      .addInterceptor(
        HttpLoggingInterceptor(logger = { logcat { it } }).apply {
          this.level = HttpLoggingInterceptor.Level.BODY
        }
      )
      .build()
  }

  @Provides
  @Singleton
  fun provideGson(): Gson = GsonBuilder().setStrictness(Strictness.LENIENT).create()

  @Provides
  @Singleton
  fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit {
    return Retrofit.Builder()
      .client(client)
      .baseUrl(SorteoApi.URL)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .build()
  }

  @Provides
  @Singleton
  fun provideFavoritosRepository(database: Database): FavoritosRepository =
    FavoritosRepositoryImpl(database.favoritoQueries)
}
