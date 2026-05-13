package me.elmanss.melate.favorites.presentation.list.compose

import android.annotation.SuppressLint
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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
import me.elmanss.melate.common.util.TestTags
import me.elmanss.melate.favorites.domain.model.FavoritoModel
import me.elmanss.melate.favorites.presentation.list.ListFavoritesScreenViewModel
import me.elmanss.melate.favorites.presentation.list.entities.ListFavUiEvent
import me.elmanss.melate.favorites.presentation.list.entities.ListFavoritesScreenState
import me.elmanss.melate.favorites.presentation.list.entities.ListFavoritesSideEffect

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun ListFavoritesScreen(
  onCreateClicked: () -> Unit,
  viewModel: ListFavoritesScreenViewModel = hiltViewModel<ListFavoritesScreenViewModel>(),
) {
  val lifecycleOwner = LocalLifecycleOwner.current
  val context = LocalContext.current
  val uiState by viewModel.state.collectAsState()
  val connectivityState by viewModel.connectivity.collectAsState(NetworkStatus.Unavailable)
  val snackbarState = remember { SnackbarHostState() }

  LaunchedEffect(key1 = Unit) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      withContext(Dispatchers.Main.immediate) {
        viewModel.sideEffect.filterNotNull().collectLatest { sideEffect ->
          logcat("ListFavoritesScreen") { sideEffect.toString() }
          when (sideEffect) {
            ListFavoritesSideEffect.LaunchCreateScreen -> {
              onCreateClicked.invoke()
              viewModel.sendEvent(ListFavUiEvent.ClearFlags)
            }

            is ListFavoritesSideEffect.ShowSnackBar -> {
              snackbarState.showSnackbar(
                message = context.getString(sideEffect.messageId),
                duration = SnackbarDuration.Short,
              )
            }
            is ListFavoritesSideEffect.ShowDeleteDialog -> {
                // Handled in content
            }
            is ListFavoritesSideEffect.ShowMultiDeleteDialog -> {
                // Handled in content
            }
          }
        }
      }
    }
  }

  ListFavoritesContent(
    uiState = uiState,
    connectivityStatus = connectivityState,
    snackbarHostState = snackbarState,
    onEvent = viewModel::sendEvent,
    formatDate = viewModel::formatDate
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListFavoritesContent(
  uiState: ListFavoritesScreenState,
  connectivityStatus: NetworkStatus,
  snackbarHostState: SnackbarHostState,
  onEvent: (ListFavUiEvent) -> Unit,
  formatDate: (FavoritoModel) -> String
) {
  val sorteoState = rememberLazyListState()
  var favToDelete by rememberSaveable { mutableStateOf<FavoritoModel?>(null) }
  var showMultiDeleteDialog by rememberSaveable { mutableStateOf(false) }

  BackHandler(enabled = uiState.multiselectEnabled) {
    onEvent(ListFavUiEvent.ExitMultiDelete)
  }

  Scaffold(
    topBar = {
      Column {
        MelateActionTopBar(title = R.string.txt_mis_sorteos) {
          if (uiState.multiselectEnabled) {
            IconButton(
              modifier = Modifier.testTag(TestTags.LIST_MULTI_DELETE_ICON),
              onClick = { showMultiDeleteDialog = true }
            ) {
              Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
            }
          }
        }

        if (!uiState.multiselectEnabled && uiState.isLoading)
          LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
      }
    },
    floatingActionButton = {
      if (!uiState.multiselectEnabled) {
        MelateActionExtendedFab(
          listState = sorteoState,
          actionOneIcon = ImageVector.vectorResource(R.drawable.cloud),
          onActionOneClicked = {
            if (connectivityStatus == NetworkStatus.Available) {
              onEvent(ListFavUiEvent.TapFetchFronNetworkEvent)
            } else {
              onEvent(ListFavUiEvent.ShowConnectivityMessage(true))
            }
          },
          actionTwoIcon = ImageVector.vectorResource(R.drawable.human_edit),
          onActionTwoClicked = { onEvent(ListFavUiEvent.TapCreateEvent) }
        )
      }
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
  ) { padding ->
    val favs = uiState.favs
    if (favs.isEmpty()) {
      Column(
        modifier = Modifier.fillMaxSize().padding(padding).padding(16F.dp).testTag(TestTags.LIST_EMPTY_STATE_CONTAINER),
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
      Column(modifier = Modifier.fillMaxSize().padding(padding)) {
        LazyColumn(state = sorteoState) {
          itemsIndexed(favs) { index, fav ->
            ListFavoriteItem(
              editableState = uiState.multiselectEnabled,
              favorite = fav,
              formatter = { formatDate(fav) },
              onChecked = { f ->
                onEvent(ListFavUiEvent.ToggleFavCheckEvent(f, index))
              },
              onLongClick = { f ->
                if (!uiState.multiselectEnabled) {
                  onEvent(ListFavUiEvent.LongTapFavEvent(f, index))
                }
              },
              onToggleSubmitted = { f ->
                onEvent(ListFavUiEvent.ToggleSubmittedEvent(f))
              },
              onClick = {
                if (!uiState.multiselectEnabled) {
                  favToDelete = fav
                }
              },
            )

            if (index < favs.lastIndex) {
              HorizontalDivider(thickness = Dp.Hairline)
            }
          }
        }
      }
    }

    favToDelete?.let { sorteo ->
      MelateSorteoActionDialog(
        { favToDelete = null },
        {
          onEvent(ListFavUiEvent.TapDeleteFavEvent(sorteo))
          favToDelete = null
        },
        R.string.txt_title_aviso,
        R.string.txt_msg_delete_fav,
        R.string.txt_action_delete,
      )
    }

    if (showMultiDeleteDialog) {
      MelateSorteoActionDialog(
        { showMultiDeleteDialog = false },
        {
          onEvent(ListFavUiEvent.TapDeleteMultipleFavs)
          showMultiDeleteDialog = false
        },
        R.string.txt_title_aviso,
        R.string.txt_msg_delete_multiple_favs,
        R.string.txt_action_delete,
      )
    }
  }
}
