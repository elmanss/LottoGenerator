package me.elmanss.melate.favorites.presentation.list.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import me.elmanss.melate.R
import me.elmanss.melate.common.data.local.FavOrigin
import me.elmanss.melate.favorites.domain.model.FavoritoModel

@Composable
fun ListFavoriteItem(
  favorite: FavoritoModel,
  formatter: (FavoritoModel) -> String,
  onLongClick: (FavoritoModel) -> Unit,
) {
  Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
    Row(
      modifier = Modifier.combinedClickable(onClick = {}, onLongClick = { onLongClick(favorite) })
    ) {
      Text(
        text =
          if (favorite.origin == FavOrigin.Random)
            favorite.sorteo.removePrefix("[").removeSuffix("]")
          else favorite.sorteo
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Image(
          painter =
            painterResource(
              if (favorite.origin == FavOrigin.Random) R.drawable.cellphone
              else R.drawable.human_edit
            ),
          contentDescription = "Origin icon",
        )
      }
    }

    if (favorite.createdAt > 0) {
      Text(text = formatter(favorite), fontSize = TextUnit(12F, TextUnitType.Sp))
    }
  }
}
