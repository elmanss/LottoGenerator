package me.elmanss.melate.home.domain.usecase

import java.util.Random
import javax.inject.Inject


class GetListId @Inject constructor(private val random: Random) {
  operator fun invoke() = random.nextInt()
}
