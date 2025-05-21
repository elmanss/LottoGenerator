package me.elmanss.melate.favorites.presentation.list.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
  modifier: Modifier = Modifier,
  editableState: Boolean = false,
  formatter: (FavoritoModel) -> String,
  onChecked: (FavoritoModel) -> Unit,
  onLongClick: (FavoritoModel) -> Unit,
  onClick: (FavoritoModel) -> Unit,
) {

  var actionState by remember { mutableStateOf(false) }

  Column(
    modifier =
      modifier
        .fillMaxWidth()
        .padding(16.dp)
        .combinedClickable(
          onClick = { onClick.invoke(favorite) },
          onLongClick = {
            actionState = true
            favorite.selected = true
            onLongClick.invoke(favorite)
          },
        )
  ) {
    Row {
      Text(
        text =
          if (favorite.origin == FavOrigin.Random)
            favorite.sorteo.removePrefix("[").removeSuffix("]")
          else favorite.sorteo
      )
      Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        if (editableState) {
          Checkbox(
            modifier = modifier.wrapContentWidth(),
            checked = actionState,
            onCheckedChange = {
              actionState = !actionState
              favorite.selected = actionState
              onChecked.invoke(favorite)
            },
          )
        } else {
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
    }

    if (favorite.createdAt > 0) {
      Text(text = formatter(favorite), fontSize = TextUnit(12F, TextUnitType.Sp))
    }
  }
}
