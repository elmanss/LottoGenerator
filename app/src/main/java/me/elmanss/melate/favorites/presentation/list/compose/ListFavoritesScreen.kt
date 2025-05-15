package me.elmanss.melate.favorites.presentation.list.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.elmanss.melate.R
import me.elmanss.melate.common.data.local.FavOrigin
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateFab
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateSorteoActionDialog
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateTopBar
import me.elmanss.melate.favorites.presentation.list.ListFavoritesScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListFavoritesScreen(
  onCreateClicked: () -> Unit,
  viewModel: ListFavoritesScreenViewModel = hiltViewModel(),
) {

  val uiState = viewModel.state.collectAsState()
  val sorteoState = rememberLazyListState()
  val snackbarState = remember { SnackbarHostState() }

  Scaffold(
    topBar = { MelateTopBar(title = R.string.txt_mis_sorteos) },
    floatingActionButton = {
      MelateFab(
        action = {
          viewModel.clearNotifications()
          onCreateClicked.invoke()
        },
        text = R.string.txt_button_mis_favs_create,
      )
    },
    snackbarHost = { SnackbarHost(snackbarState) },
  ) {
    Column(modifier = Modifier.fillMaxWidth().padding(it)) {
      val favs = uiState.value.favs

      LazyColumn(state = sorteoState) {
        itemsIndexed(favs) { index, fav ->
          Row(
            modifier =
              Modifier.fillMaxWidth()
                .padding(16.dp)
                .combinedClickable(onClick = {}, onLongClick = { viewModel.showWarning(fav) })
          ) {
            Text(
              text =
                if (fav.origin == FavOrigin.Random) fav.sorteo.removePrefix("[").removeSuffix("]")
                else fav.sorteo
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Image(
                painter =
                  painterResource(
                    if (fav.origin == FavOrigin.Random) R.drawable.cellphone
                    else R.drawable.human_edit
                  ),
                contentDescription = "Origin icon",
              )
            }
          }
          if (index < favs.lastIndex) {
            HorizontalDivider(thickness = Dp.Hairline)
          }
        }
      }

      uiState.value.favToDelete?.let { sorteo ->
        MelateSorteoActionDialog(
          { viewModel.dismissWarning() },
          { viewModel.deleteFavs(sorteo) },
          R.string.txt_title_aviso,
          R.string.txt_msg_delete_fav,
          R.string.txt_action_delete,
        )
      }

      if (uiState.value.showDeletionSuccess) {
        val successMsg = stringResource(R.string.txt_fav_deletion_success)
        LaunchedEffect(true) {
          val result =
            snackbarState.showSnackbar(message = successMsg, duration = SnackbarDuration.Short)
          when (result) {
            SnackbarResult.Dismissed -> {
              viewModel.showDeletionMessage(false)
            }

            SnackbarResult.ActionPerformed -> {}
          }
        }
      }
    }
  }
}
