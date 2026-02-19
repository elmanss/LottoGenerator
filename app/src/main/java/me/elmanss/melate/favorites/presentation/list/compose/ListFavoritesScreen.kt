package me.elmanss.melate.favorites.presentation.list.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext
import logcat.logcat
import me.elmanss.melate.R
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateActionExtendedFab
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateActionTopBar
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateSorteoActionDialog
import me.elmanss.melate.common.presentation.ui.compose.ui.theme.Gray
import me.elmanss.melate.common.util.NetworkStatus
import me.elmanss.melate.favorites.presentation.list.ListFavoritesScreenViewModel
import me.elmanss.melate.favorites.presentation.list.entities.ListFavUiEvent
import me.elmanss.melate.favorites.presentation.list.entities.ListFavoritesSideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListFavoritesScreen(
  onCreateClicked: () -> Unit,
  viewModel: ListFavoritesScreenViewModel = hiltViewModel<ListFavoritesScreenViewModel>(),
) {
  val lifecycleOwner = LocalLifecycleOwner.current
  val res = LocalResources.current
  val uiState = viewModel.state.collectAsState()
  val connectivityState by viewModel.connectivity.collectAsState(NetworkStatus.Unavailable)
  val sorteoState = rememberLazyListState()
  val snackbarState = remember { SnackbarHostState() }
  var multiselectState by remember { mutableStateOf(false) }

  LaunchedEffect(key1 = Unit) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      withContext(Dispatchers.Main.immediate) {
        viewModel.sideEffect.filterNotNull().collectLatest { sideEffect ->
          logcat("ListFavoritesScreen") { sideEffect.toString() }
          when (sideEffect) {
            ListFavoritesSideEffect.OnMultiDeleteCompleted -> {
              viewModel.sendEvent(ListFavUiEvent.DisableMultiDelete)
              viewModel.sendEvent(ListFavUiEvent.HideMultiDeleteFavDialog)
              viewModel.sendEvent(ListFavUiEvent.ClearFlags)
            }

            ListFavoritesSideEffect.LaunchCreateScreen -> {
              onCreateClicked.invoke()
              viewModel.sendEvent(ListFavUiEvent.ClearFlags)
            }

            is ListFavoritesSideEffect.ShowSnackBar -> {
              val msg =
                sideEffect.message.ifEmpty { res.getString(R.string.txt_fav_deletion_success) }
              snackbarState.showSnackbar(message = msg, duration = SnackbarDuration.Short)
            }
          }
        }
      }
    }
  }

  BackHandler(enabled = multiselectState) { viewModel.sendEvent(ListFavUiEvent.DisableMultiDelete) }

  Scaffold(
    topBar = {
      Column {
        MelateActionTopBar(title = R.string.txt_mis_sorteos) {
          if (multiselectState) {
            IconButton(
              onClick = { viewModel.sendEvent(ListFavUiEvent.ClickConfirmMultiDeleteEvent) }
            ) {
              Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
            }
          }
        }

        if (!multiselectState && uiState.value.isLoading)
          LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
      }
    },
    floatingActionButton = {
      if (!multiselectState) {
        MelateActionExtendedFab(
          listState = sorteoState,
          actionOneIcon = ImageVector.vectorResource(R.drawable.cloud),
          onActionOneClicked = {
            if (connectivityState == NetworkStatus.Available) {
              viewModel.sendEvent(ListFavUiEvent.ClickMultiDeleteEvent)
            } else {
              viewModel.sendEvent(ListFavUiEvent.ShowConnectivityMessage(true))
            }
          },
          actionTwoIcon = ImageVector.vectorResource(R.drawable.human_edit),
        ) {
          viewModel.sendEvent(ListFavUiEvent.ClickCreateEvent)
        }
      }
    },
    snackbarHost = { SnackbarHost(snackbarState) },
  ) {
    multiselectState = uiState.value.multiselectEnabled
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
            ListFavoriteItem(
              editableState = multiselectState,
              favorite = fav,
              formatter = { viewModel.formatDate(fav) },
              onChecked = { f -> viewModel.sendEvent(ListFavUiEvent.SelectFavEvent(fav, index)) },
              onLongClick = { f ->
                if (!multiselectState) {
                  viewModel.sendEvent(ListFavUiEvent.LongClickFavEvent(fav, index))
                }
              },
            ) {
              if (!multiselectState) {
                viewModel.sendEvent(ListFavUiEvent.ClickFavEvent(fav))
              }
            }

            if (index < favs.lastIndex) {
              HorizontalDivider(thickness = Dp.Hairline)
            }
          }
        }
      }
    }

    uiState.value.clickedFav?.let { sorteo ->
      MelateSorteoActionDialog(
        { viewModel.sendEvent(ListFavUiEvent.HideDeleteFavDialog) },
        { viewModel.sendEvent(ListFavUiEvent.ClickDeleteFavEvent(sorteo)) },
        R.string.txt_title_aviso,
        R.string.txt_msg_delete_fav,
        R.string.txt_action_delete,
      )
    }
  }

  if (uiState.value.showMultiDeletionPrompt) {
    MelateSorteoActionDialog(
      { viewModel.sendEvent(ListFavUiEvent.HideMultiDeleteFavDialog) },
      { viewModel.sendEvent(ListFavUiEvent.DeleteMultipleFavs) },
      R.string.txt_title_aviso,
      "Se eliminaran los sorteos seleccionados.",
      R.string.txt_action_delete,
    )
  }
}
