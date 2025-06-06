package me.elmanss.melate.home.presentation.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.elmanss.melate.R
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateActionTopBar
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateFab
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateSorteoActionDialog
import me.elmanss.melate.home.presentation.HomeScreenViewModel
import me.elmanss.melate.home.presentation.HomeUiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigateToFavs: () -> Unit, viewModel: HomeScreenViewModel = hiltViewModel()) {

  val uiState = viewModel.state.collectAsState()
  val sorteoState = rememberLazyListState()
  val refreshState = rememberPullToRefreshState()
  var isRefreshing by remember { mutableStateOf(false) }
  val snackbarState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()
  var multiselectState by rememberSaveable { mutableStateOf(false) }

  BackHandler(enabled = multiselectState) { viewModel.sendEvent(HomeUiEvent.ExitMultiSelect) }

  Scaffold(
    topBar = {
      MelateActionTopBar(title = R.string.app_name) {
        if (multiselectState) {
          IconButton(onClick = { viewModel.sendEvent(HomeUiEvent.ConfirmMultiSelect) }) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
          }
        }
      }
    },
    floatingActionButton = {
      MelateFab(
        listState = sorteoState,
        action = { viewModel.sendEvent(HomeUiEvent.GoToFavs) },
        text = R.string.txt_button_mis_favs,
      )
    },
    snackbarHost = { SnackbarHost(snackbarState) },
  ) {
    multiselectState = uiState.value.multiSelectMode

    Column(modifier = Modifier.fillMaxWidth().padding(it)) {
      val sorteos = uiState.value.sorteos
      PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
          isRefreshing = true
          coroutineScope.launch {
            delay(1500)
            viewModel.sendEvent(HomeUiEvent.RefreshSorteos)
            isRefreshing = false
          }
        },
        state = refreshState,
        indicator = {
          Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            state = refreshState,
          )
        },
      ) {
        LazyColumn(state = sorteoState) {
          itemsIndexed(items = sorteos, key = { index, _ -> index.toHexString() }) { index, sorteo
            ->
            HomeListItem(
              selectableMode = multiselectState,
              sorteo = sorteo,
              onChecked = { s -> HomeUiEvent.SelectSorteo(s, index) },
              onClick = { s ->
                if (!multiselectState) {
                  viewModel.sendEvent(HomeUiEvent.ShowSaveSorteoDialog(s))
                }
              },
            ) { s ->
              if (!multiselectState) {
                viewModel.sendEvent(HomeUiEvent.EnableSorteoMultiSelect(s, index))
              }
            }

            if (index < sorteos.lastIndex) {
              HorizontalDivider(thickness = Dp.Hairline)
            }
          }
        }
      }

      uiState.value.clickedSorteo?.let { sorteo ->
        MelateSorteoActionDialog(
          { viewModel.sendEvent(HomeUiEvent.HideSaveSorteoDialog) },
          { viewModel.sendEvent(HomeUiEvent.ConfirmSaveSorteo(sorteo)) },
          R.string.txt_title_aviso,
          R.string.txt_msg_add_to_fav,
          R.string.txt_action_add,
        )
      }

      if (uiState.value.showStorageSuccess) {
        val successMsg = stringResource(R.string.txt_sorteo_success)
        LaunchedEffect(true) {
          val result =
            snackbarState.showSnackbar(message = successMsg, duration = SnackbarDuration.Short)
          when (result) {
            SnackbarResult.Dismissed -> {
              viewModel.sendEvent(HomeUiEvent.DisplaySuccessMessage(visible = true))
            }

            SnackbarResult.ActionPerformed -> {}
          }
        }
      }
    }
  }

  if (uiState.value.onGoToFav) {
    onNavigateToFavs.invoke()
    viewModel.sendEvent(HomeUiEvent.ClearFlags)
  }
}
