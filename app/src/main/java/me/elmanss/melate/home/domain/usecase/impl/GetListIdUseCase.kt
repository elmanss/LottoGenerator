package me.elmanss.melate.home.domain.usecase.impl

import java.util.Random
import javax.inject.Inject

class GetListIdUseCase @Inject constructor(private val random: Random) {
  operator fun invoke() = random.nextInt()
}
