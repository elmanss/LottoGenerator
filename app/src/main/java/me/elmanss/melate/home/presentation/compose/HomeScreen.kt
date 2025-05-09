package me.elmanss.melate.home.presentation.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.elmanss.melate.R
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateFab
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateTopBar
import me.elmanss.melate.home.presentation.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = hiltViewModel()) {

  val uiState = viewModel.state.collectAsState()
  val sorteoState = rememberLazyListState()
  val refreshState = rememberPullToRefreshState()
  var isRefreshing by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  Scaffold(
    topBar = { MelateTopBar(title = R.string.app_name) },
    floatingActionButton = { MelateFab(action = {}, text = R.string.txt_button_mis_favs) },
  ) {
    Column(modifier = Modifier.fillMaxWidth().padding(it)) {
      val sorteos = uiState.value.sorteos
      PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
          isRefreshing = true
          coroutineScope.launch {
            delay(1500)
            viewModel.fetchSorteos()
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
          itemsIndexed(sorteos) { index, sorteo ->
            Text(modifier = Modifier.padding(16.dp), text = sorteo.numeros.joinToString())
            if (index < sorteos.lastIndex) {
              HorizontalDivider(thickness = Dp.Hairline)
            }
          }
        }
      }
    }
  }
}
