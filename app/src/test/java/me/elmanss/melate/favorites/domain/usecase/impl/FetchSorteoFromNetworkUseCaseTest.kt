package me.elmanss.melate.favorites.domain.usecase.impl

import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.test.runTest
import me.elmanss.melate.common.data.local.FavOrigin
import me.elmanss.melate.home.data.repository.SorteoRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure
import strikt.assertions.isSuccess

@OptIn(ExperimentalTime::class)
@ExtendWith(MockKExtension::class)
class FetchSorteoFromNetworkUseCaseTest {

  @MockK private lateinit var sorteoRepository: SorteoRepository

  @MockK private lateinit var clock: Clock

  private lateinit var useCase: FetchSorteoFromNetworkUseCase

  @BeforeEach
  fun setUp() {
    useCase = FetchSorteoFromNetworkUseCase(sorteoRepository, clock)
  }

  @Nested
  inner class Invoke {
    @Test
    fun `SHOULD return success with mapped model WHEN repository fetch is successful`() = runTest {
      // GIVEN: A successful result from the repository and a fixed time
      val fakeSorteoData = listOf(1, 2, 3, 4, 5, 6)
      val repositoryResult = Result.success(fakeSorteoData)
      val fixedInstant = Instant.parse("2023-01-01T00:00:00Z")

      coEvery { sorteoRepository.fetchSorteos() } returns repositoryResult
      every { clock.now() } returns fixedInstant

      // WHEN: The use case is invoked
      val result = useCase()

      // THEN: The result should be a success and contain a correctly mapped FavoritoModel
      expectThat(result).isSuccess().and {
        get { sorteo }.isEqualTo("1, 2, 3, 4, 5, 6")
        get { origin }.isEqualTo(FavOrigin.Network)
        get { createdAt }.isEqualTo(fixedInstant.toEpochMilliseconds())
      }
    }

    @Test
    fun `SHOULD return failure WHEN repository fetch fails`() = runTest {
      // GIVEN: A failure result from the repository
      val networkException = Exception("Network Error")
      val repositoryResult = Result.failure<List<Int>>(networkException)
      coEvery { sorteoRepository.fetchSorteos() } returns repositoryResult

      // WHEN: The use case is invoked
      val result = useCase()

      // THEN: The result should be a failure and contain the original exception
      expectThat(result).isFailure().isEqualTo(networkException)
    }
  }
}
