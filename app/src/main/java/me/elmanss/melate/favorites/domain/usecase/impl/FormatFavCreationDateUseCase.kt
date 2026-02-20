package me.elmanss.melate.favorites.domain.usecase.impl

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class FormatFavCreationDateUseCase(private val formatter: DateTimeFormatter) {
  operator fun invoke(createdAt: Long): String {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault())
      .format(formatter)
  }
}
