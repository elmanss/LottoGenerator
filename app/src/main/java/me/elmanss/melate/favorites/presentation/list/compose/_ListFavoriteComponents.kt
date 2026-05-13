package me.elmanss.melate.favorites.presentation.list.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import me.elmanss.melate.R
import me.elmanss.melate.common.data.local.FavOrigin
import me.elmanss.melate.common.util.TestTags
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
  onToggleSubmitted: (FavoritoModel) -> Unit,
) {

  var actionState by rememberSaveable { mutableStateOf(false) }

  // Visual distinction: dim items that are already submitted
  val contentAlpha = if (favorite.isSubmitted) 0.5f else 1.0f

  Column(
    modifier =
      modifier
        .fillMaxWidth()
        .combinedClickable(
          onClick = { onClick.invoke(favorite) },
          onLongClick = {
            actionState = true
            favorite.selected = true
            onLongClick.invoke(favorite)
          },
        )
        .padding(16.dp)
        .alpha(contentAlpha)
        .testTag(TestTags.FAV_ITEM_ROW)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        modifier = Modifier.weight(1f),
        text =
          if (favorite.origin == FavOrigin.Random)
            favorite.sorteo.removePrefix("[").removeSuffix("]")
          else favorite.sorteo,
        style = MaterialTheme.typography.bodyLarge
      )
      
      Row(
        modifier = Modifier.wrapContentWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        if (!editableState) {
          // Submission toggle button
          IconButton(
            modifier = Modifier.size(24.dp).testTag(TestTags.FAV_ITEM_SUBMISSION_TOGGLE),
            onClick = { onToggleSubmitted(favorite) }
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Toggle submitted status",
              tint = if (favorite.isSubmitted) Color(0xFF4CAF50) else Color.Gray.copy(alpha = 0.5f)
            )
          }
          
          Spacer(modifier = Modifier.width(12.dp))
          
          Image(
            modifier = Modifier.size(24.dp).testTag(TestTags.FAV_ITEM_ORIGIN_ICON),
            painter =
              painterResource(
                when (favorite.origin) {
                  FavOrigin.Random -> R.drawable.cellphone
                  FavOrigin.Network -> R.drawable.cloud
                  else -> R.drawable.human_edit
                }
              ),
            contentDescription = "Origin icon",
          )
        } else {
          Checkbox(
            modifier = Modifier.wrapContentWidth().testTag(TestTags.FAV_ITEM_CHECKBOX),
            checked = actionState,
            onCheckedChange = {
              actionState = it
              favorite.selected = it
              onChecked.invoke(favorite)
            },
          )
        }
      }
    }

    if (favorite.createdAt > 0) {
      Text(
        text = formatter(favorite), 
        fontSize = TextUnit(12F, TextUnitType.Sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
