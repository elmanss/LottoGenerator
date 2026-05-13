package me.elmanss.melate.home.presentation.compose

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logcat.logcat
import me.elmanss.melate.R
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateActionTopBar
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateFab
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateSorteoActionDialog
import me.elmanss.melate.common.util.TestTags
import me.elmanss.melate.home.domain.model.SorteoModel
import me.elmanss.melate.home.presentation.HomeScreenViewModel
import me.elmanss.melate.home.presentation.entities.HomeScreenSideEffect
import me.elmanss.melate.home.presentation.entities.HomeScreenState
import me.elmanss.melate.home.presentation.entities.HomeUiEvent

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun HomeScreen(
  onNavigateToFavs: () -> Unit,
  viewModel: HomeScreenViewModel = hiltViewModel<HomeScreenViewModel>(),
) {

  val lifecycleOwner = LocalLifecycleOwner.current
  val context = LocalContext.current

  val uiState by viewModel.state.collectAsState()
  val snackbarState = remember { SnackbarHostState() }

  LaunchedEffect(key1 = Unit) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      withContext(Dispatchers.Main.immediate) {
        viewModel.sideEffect.filterNotNull().collectLatest { sideEffect ->
          logcat("HomeScreen") { sideEffect.toString() }
          when (sideEffect) {
            HomeScreenSideEffect.GoToFavs -> {
              onNavigateToFavs.invoke()
            }

            is HomeScreenSideEffect.ShowSnackBar -> {
              snackbarState.showSnackbar(context.getString(sideEffect.messageId))
            }

            is HomeScreenSideEffect.ShowSaveFavoriteDialog -> {
                // Handled in content
            }
          }
        }
      }
    }
  }

  HomeScreenContent(
    uiState = uiState,
    snackbarHostState = snackbarState,
    onEvent = viewModel::sendEvent
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
  uiState: HomeScreenState,
  snackbarHostState: SnackbarHostState,
  onEvent: (HomeUiEvent) -> Unit
) {
  val sorteoState = rememberLazyListState()
  val refreshState = rememberPullToRefreshState()
  val coroutineScope = rememberCoroutineScope()
  var sorteoToSave by rememberSaveable { mutableStateOf<SorteoModel?>(null) }

  BackHandler(enabled = uiState.multiSelectModeEnabled) {
    onEvent(HomeUiEvent.ExitMultiSelectEvent)
  }

  Scaffold(
    topBar = {
      MelateActionTopBar(title = R.string.app_name) {
        if (uiState.multiSelectModeEnabled) {
          IconButton(
            modifier = Modifier.testTag(TestTags.HOME_SAVE_TOPBAR_ICON),
            onClick = { onEvent(HomeUiEvent.TapConfirmMultiSelectEvent) }
          ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
          }
        }
      }
    },
    floatingActionButton = {
      MelateFab(
        listState = sorteoState,
        action = { onEvent(HomeUiEvent.TapGoToFavsEvent) },
        text = R.string.txt_button_mis_favs,
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
  ) { padding ->
    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
      val sorteos = uiState.sorteos
      PullToRefreshBox(
        modifier = Modifier.fillMaxSize().testTag(TestTags.HOME_PULL_REFRESH),
        isRefreshing = uiState.isRefreshing,
        onRefresh = {
          onEvent(HomeUiEvent.SwipeToRefreshSorteosEvent)
        },
        state = refreshState,
        indicator = {
          Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = uiState.isRefreshing,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            state = refreshState,
          )
        },
      ) {
        LazyColumn(modifier = Modifier.fillMaxSize(), state = sorteoState) {
          itemsIndexed(items = sorteos, key = { _, item -> item.id }) { index, sorteo ->
            HomeListItem(
              selectableMode = uiState.multiSelectModeEnabled,
              sorteo = sorteo,
              onChecked = { s ->
                onEvent(HomeUiEvent.ToggleSorteoCheckEvent(s))
              },
              onClick = { s ->
                if (!uiState.multiSelectModeEnabled) {
                  onEvent(HomeUiEvent.TapSorteoEvent(s))
                }
              },
              onLongClick = { s ->
                if (!uiState.multiSelectModeEnabled) {
                  onEvent(HomeUiEvent.LongTapSorteoEvent(s))
                }
              }
            )

            if (index < sorteos.lastIndex) {
              HorizontalDivider(thickness = Dp.Hairline)
            }
          }
        }
      }

      sorteoToSave?.let { sorteo ->
        MelateSorteoActionDialog(
          { sorteoToSave = null },
          {
            onEvent(HomeUiEvent.TapAddSorteoEvent(sorteo))
            sorteoToSave = null
          },
          R.string.txt_title_aviso,
          R.string.txt_msg_add_to_fav,
          R.string.txt_action_add,
        )
      }
    }
  }
}
