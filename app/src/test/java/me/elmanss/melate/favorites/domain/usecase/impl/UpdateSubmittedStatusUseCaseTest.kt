package me.elmanss.melate.favorites.domain.usecase.impl

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import me.elmanss.melate.common.domain.repository.FavoritosRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class UpdateSubmittedStatusUseCaseTest {

  @MockK private lateinit var repository: FavoritosRepository
  private lateinit var useCase: UpdateSubmittedStatusUseCase

  @BeforeEach
  fun setUp() {
    useCase = UpdateSubmittedStatusUseCase(repository)
  }

  @Test
  fun `SHOULD call repository update WHEN use case is invoked`() = runTest {
    // GIVEN
    val id = 1L
    val isSubmitted = true
    coEvery { repository.updateSubmittedStatus(any(), any()) } returns Unit

    // WHEN
    useCase(id, isSubmitted)

    // THEN
    coVerify { repository.updateSubmittedStatus(id, isSubmitted) }
  }
}
