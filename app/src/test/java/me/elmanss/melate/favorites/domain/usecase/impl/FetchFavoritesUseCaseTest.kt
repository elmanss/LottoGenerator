package me.elmanss.melate.favorites.domain.usecase.impl

import app.cash.sqldelight.Query
import app.cash.turbine.test
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
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
      // GIVEN
      val fakeDbEntities =
        listOf(
          Favorito(1L, "1,2,3", FavOrigin.Manual, 100L, 0L),
          Favorito(2L, "4,5,6", FavOrigin.Network, 200L, 1L),
          Favorito(3L, "7,8,9", FavOrigin.Random, 300L, 0L),
        )
      val mockQuery: Query<Favorito> = mockk { every { executeAsList() } returns fakeDbEntities }
      every { repository.selectAllFavoritos() } returns flowOf(mockQuery)

      // WHEN
      val resultFlow = useCase()

      // THEN
      resultFlow.test {
        val emission = awaitItem()
        expectThat(emission) {
          hasSize(3)
          get(0).and {
            get { id }.isEqualTo(1L)
            get { sorteo }.isEqualTo("1,2,3")
            get { origin }.isEqualTo(FavOrigin.Manual)
            get { createdAt }.isEqualTo(100L)
            get { isSubmitted }.isEqualTo(false)
            get { selected }.isEqualTo(false)
          }
          get(1).and {
            get { id }.isEqualTo(2L)
            get { isSubmitted }.isEqualTo(true)
          }
        }
        awaitComplete()
      }
    }

    @Test
    fun `SHOULD emit updated lists WHEN repository emits new data`() = runTest {
      // GIVEN
      val queryFlow =
        MutableStateFlow<Query<Favorito>>(
          mockk {
            every { executeAsList() } returns
              listOf(Favorito(1L, "1,2,3", FavOrigin.Manual, 100L, 0L))
          }
        )
      every { repository.selectAllFavoritos() } returns queryFlow

      // WHEN
      useCase().test {
        // THEN: First emission
        expectThat(awaitItem()[0].isSubmitted).isEqualTo(false)

        // GIVEN: Repository emits updated data (e.g. item was submitted)
        queryFlow.value = mockk {
          every { executeAsList() } returns
            listOf(Favorito(1L, "1,2,3", FavOrigin.Manual, 100L, 1L))
        }

        // THEN: Second emission reflects the change
        expectThat(awaitItem()[0].isSubmitted).isEqualTo(true)
      }
    }

    @Test
    fun `SHOULD return empty list WHEN repository emits empty list`() = runTest {
      // GIVEN
      val mockQuery: Query<Favorito> = mockk { every { executeAsList() } returns emptyList() }
      every { repository.selectAllFavoritos() } returns flowOf(mockQuery)

      // WHEN
      val resultFlow = useCase()

      // THEN
      resultFlow.test {
        val emission = awaitItem()
        expectThat(emission).isEmpty()
        awaitComplete()
      }
    }
  }
}
