package me.elmanss.melate.home.domain.usecase.impl

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import me.elmanss.melate.home.data.repository.SorteoRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.all
import strikt.assertions.hasSize
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import strikt.assertions.map

@ExtendWith(MockKExtension::class)
internal class FetchSorteosUseCaseTest {

  @MockK private lateinit var repository: SorteoRepository

  private lateinit var useCase: FetchSorteosUseCase

  @BeforeEach
  fun setUp() {
    useCase = FetchSorteosUseCase(repository)
  }

  @Test
  fun `invoke SHOULD return flow with sorteo models WHEN repository fetch is successful`() =
    runTest {
      // GIVEN: A successful result from the repository
      val fakeSorteoData = listOf(1, 2, 3, 4, 5, 6)
      val repositoryResult = Result.success(fakeSorteoData)
      coEvery { repository.fetchSorteos() } returns repositoryResult

      // WHEN: The use case is invoked, test the flow with Turbine
      val resultFlow = useCase()

      resultFlow.test {
        // THEN: Assert the emitted item is correct
        val emission = awaitItem()

        expectThat(emission).hasSize(30)
        expectThat(emission).map { it.numeros }.all { isEqualTo(fakeSorteoData) }

        // Ensure the flow completes after the single emission
        awaitComplete()
      }

      // VERIFY: The repository was called 30 times
      coVerify(exactly = 30) { repository.fetchSorteos() }
    }

  @Test
  fun `invoke SHOULD return flow with an empty list WHEN repository fetch fails`() = runTest {
    // GIVEN: A failure result from the repository
    val repositoryResult = Result.failure<List<Int>>(Exception("Network Error"))
    coEvery { repository.fetchSorteos() } returns repositoryResult

    // WHEN: The use case is invoked
    val resultFlow = useCase()

    resultFlow.test {
      // THEN: Assert the emitted list is empty
      val emission = awaitItem()
      expectThat(emission).isEmpty()
      awaitComplete()
    }

    // VERIFY: The repository was still called 30 times
    coVerify(exactly = 30) { repository.fetchSorteos() }
  }
}
