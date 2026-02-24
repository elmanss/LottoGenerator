package me.elmanss.melate.favorites.domain.usecase.impl

import app.cash.sqldelight.Query
import app.cash.turbine.test
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import me.elmanss.melate.common.data.local.FavOrigin
import me.elmanss.melate.common.domain.repository.FavoritosRepository
import me.elmanss.melate.data.Favorito
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.get
import strikt.assertions.hasSize
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo

@ExtendWith(MockKExtension::class)
class FetchFavoritesUseCaseTest {

  @MockK private lateinit var repository: FavoritosRepository

  private lateinit var useCase: FetchFavoritesUseCase

  @BeforeEach
  fun setUp() {
    useCase = FetchFavoritesUseCase(repository)
  }

  @Nested
  inner class Invoke {
    @Test
    fun `SHOULD return flow of mapped models WHEN repository emits valid data`() = runTest {
      // GIVEN: The repository emits a list of valid, real DB entities
      val fakeDbEntities =
        listOf(
          Favorito(1L, "1,2,3", "Manual", 100L),
          Favorito(2L, "4,5,6", "Network", 200L),
          Favorito(3L, "7,8,9", "Random", 300L),
        )
      val mockQuery: Query<Favorito> = mockk { every { executeAsList() } returns fakeDbEntities }
      every { repository.selectAllFavoritos() } returns flowOf(mockQuery)

      // WHEN: The use case is invoked
      val resultFlow = useCase()

      // THEN: The flow should emit a correctly mapped list of domain models
      resultFlow.test {
        val emission = awaitItem()
        expectThat(emission) {
          hasSize(3)
          get(0).get { origin }.isEqualTo(FavOrigin.Manual)
          get(1).get { origin }.isEqualTo(FavOrigin.Network)
          get(2).get { origin }.isEqualTo(FavOrigin.Random)
        }
        awaitComplete()
      }
    }

    @Test
    fun `SHOULD map to Unknown WHEN repository emits invalid origin`() = runTest {
      // GIVEN: The repository emits an entity with an unknown origin string
      val fakeDbEntities = listOf(Favorito(1L, "1,2,3", "LegacySystem", 100L))
      val mockQuery: Query<Favorito> = mockk { every { executeAsList() } returns fakeDbEntities }
      every { repository.selectAllFavoritos() } returns flowOf(mockQuery)

      // WHEN: The use case is invoked
      val resultFlow = useCase()

      // THEN: The invalid origin should be mapped to FavOrigin.Unknown
      resultFlow.test {
        val emission = awaitItem()
        expectThat(emission) {
          hasSize(1)
          get(0).get { origin }.isEqualTo(FavOrigin.Unknown)
        }
        awaitComplete()
      }
    }

    @Test
    fun `SHOULD return empty list WHEN repository emits empty list`() = runTest {
      // GIVEN: The repository emits an empty list
      val mockQuery: Query<Favorito> = mockk { every { executeAsList() } returns emptyList() }
      every { repository.selectAllFavoritos() } returns flowOf(mockQuery)

      // WHEN: The use case is invoked
      val resultFlow = useCase()

      // THEN: The flow should emit an empty list
      resultFlow.test {
        val emission = awaitItem()
        expectThat(emission).isEmpty()
        awaitComplete()
      }
    }
  }
}
