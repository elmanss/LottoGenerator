package me.elmanss.melate.favorites.presentation.list.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import me.elmanss.melate.R
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateFab
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateSorteoActionDialog
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateTopBar
import me.elmanss.melate.common.presentation.ui.compose.ui.theme.Gray
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
    val favs = uiState.value.favs
    if (favs.isEmpty()) {
      Column(
        modifier = Modifier.fillMaxSize().padding(it).padding(16F.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        Image(
          painter = painterResource(R.drawable.heart_plus_24px),
          modifier =
            Modifier.size(dimensionResource(R.dimen.key_size)).align(Alignment.CenterHorizontally),
          contentDescription = "Add fav icon",
        )
        Text(
          color = Gray,
          fontSize = dimensionResource(R.dimen.info_text_size).value.sp,
          text = stringResource(R.string.txt_empty_fav_msg),
          textAlign = TextAlign.Center,
        )
      }
    } else {
      Column(modifier = Modifier.fillMaxSize().padding(it)) {
        LazyColumn(state = sorteoState) {
          itemsIndexed(favs) { index, fav ->
            ListFavoriteItem(favorite = fav, formatter = { viewModel.formatDate(fav) }) {
              viewModel.showWarning(fav)
            }

            if (index < favs.lastIndex) {
              HorizontalDivider(thickness = Dp.Hairline)
            }
          }
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
