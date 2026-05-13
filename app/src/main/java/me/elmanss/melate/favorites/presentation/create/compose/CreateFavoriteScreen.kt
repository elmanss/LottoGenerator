package me.elmanss.melate.favorites.presentation.create.compose

import android.annotation.SuppressLint
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
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
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateSorteoActionDialog
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateTopBar
import me.elmanss.melate.common.presentation.ui.compose.ui.theme.melateRed
import me.elmanss.melate.common.util.TestTags
import me.elmanss.melate.favorites.presentation.create.CreateFavoriteScreenViewModel
import me.elmanss.melate.favorites.presentation.create.entities.CreateFavoriteScreenState
import me.elmanss.melate.favorites.presentation.create.entities.CreateFavSideEffect
import me.elmanss.melate.favorites.presentation.create.entities.CreateFavUiEvent

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun CreateFavoriteScreen(
  viewModel: CreateFavoriteScreenViewModel = hiltViewModel<CreateFavoriteScreenViewModel>()
) {

  val uiState by viewModel.state.collectAsState()
  val snackbarState = remember { SnackbarHostState() }
  val context = LocalContext.current

  val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
  val lifecycleOwner = LocalLifecycleOwner.current

  LaunchedEffect(key1 = Unit) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      withContext(Dispatchers.Main.immediate) {
        viewModel.sideEffect.filterNotNull().collectLatest { sideEffect ->
          logcat("CreateFavoriteScreen") { sideEffect.toString() }
          when (sideEffect) {
            CreateFavSideEffect.NavigateBack -> {
              onBackPressedDispatcher?.onBackPressed()
            }

            is CreateFavSideEffect.ShowSnackbar -> {
              snackbarState.showSnackbar(
                message = context.getString(sideEffect.messageId),
                duration = SnackbarDuration.Short,
              )
            }
          }
        }
      }
    }
  }

  CreateFavoriteContent(
    uiState = uiState,
    snackbarHostState = snackbarState,
    onEvent = viewModel::sendEvent
  )
}

@Composable
fun CreateFavoriteContent(
  uiState: CreateFavoriteScreenState,
  snackbarHostState: SnackbarHostState,
  onEvent: (CreateFavUiEvent) -> Unit
) {
  Scaffold(
    topBar = { MelateTopBar(R.string.txt_title_fav_create) },
    snackbarHost = { SnackbarHost(snackbarHostState) },
  ) { paddingValues ->
    ConstraintLayout(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
      val (
        largeText,
        statusText,
        one,
        two,
        three,
        four,
        five,
        six,
        seven,
        eight,
        nine,
        zero,
        backspace,
        ok) =
        createRefs()

      Box(
        modifier =
          Modifier.fillMaxWidth().constrainAs(largeText) {
            bottom.linkTo(statusText.top)
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
          },
        contentAlignment = Alignment.Center,
      ) {
        Text(
          uiState.keyboardInput,
          modifier = Modifier.testTag(TestTags.CREATE_INPUT_DISPLAY),
          fontSize = dimensionResource(R.dimen.key_number_font_size).value.sp,
          color = melateRed(),
        )
      }

      Box(
        modifier =
          Modifier.requiredHeight(48.dp).fillMaxWidth().constrainAs(statusText) {
            bottom.linkTo(one.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
          },
        contentAlignment = Alignment.Center,
      ) {
        if (uiState.numbers.isNotEmpty()) {
          Text(
            modifier = Modifier.wrapContentHeight().fillMaxWidth(),
            text = uiState.numbers.joinToString(),
            color = melateRed(),
            fontSize = TextUnit(20F, TextUnitType.Sp),
            textAlign = TextAlign.Center,
          )
        }
      }

      // Keypad Implementation
      val keypad = listOf(
        Triple("1", one, four), Triple("2", two, five), Triple("3", three, six),
        Triple("4", four, seven), Triple("5", five, eight), Triple("6", six, nine),
        Triple("7", seven, backspace), Triple("8", eight, zero), Triple("9", nine, ok)
      )

      keypad.forEachIndexed { index, item ->
        TextButton(
          onClick = { onEvent(CreateFavUiEvent.TapDigit(item.first)) },
          modifier = Modifier.height(dimensionResource(R.dimen.key_size))
            .testTag("${TestTags.CREATE_NUMPAD_0.dropLast(1)}${item.first}")
            .constrainAs(item.second) {
              bottom.linkTo(item.third.top)
              when (index % 3) {
                0 -> { start.linkTo(parent.start); end.linkTo(keypad[index+1].second.start) }
                1 -> { start.linkTo(keypad[index-1].second.end); end.linkTo(keypad[index+1].second.start) }
                2 -> { start.linkTo(keypad[index-1].second.end); end.linkTo(parent.end) }
              }
            }
        ) {
          Text(text = item.first, color = melateRed(), textAlign = TextAlign.Center)
        }
      }

      // Bottom Row
      TextButton(
        onClick = { onEvent(CreateFavUiEvent.TapDelete) },
        modifier = Modifier.height(dimensionResource(R.dimen.key_size))
          .testTag(TestTags.CREATE_KEY_BACKSPACE)
          .constrainAs(backspace) {
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(zero.start)
          },
      ) {
        Image(painterResource(R.drawable.backspace), "Backspace")
      }

      TextButton(
        onClick = { onEvent(CreateFavUiEvent.TapDigit("0")) },
        modifier = Modifier.height(dimensionResource(R.dimen.key_size))
          .testTag(TestTags.CREATE_NUMPAD_0)
          .constrainAs(zero) {
            bottom.linkTo(parent.bottom)
            start.linkTo(backspace.end)
            end.linkTo(ok.start)
          },
      ) {
        Text(text = "0", color = melateRed(), textAlign = TextAlign.Center)
      }

      TextButton(
        onClick = { onEvent(CreateFavUiEvent.TapNext) },
        modifier = Modifier.height(dimensionResource(R.dimen.key_size))
          .testTag(TestTags.CREATE_KEY_NEXT)
          .constrainAs(ok) {
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end)
            start.linkTo(zero.end)
          },
      ) {
        val img = if (uiState.numbers.size == 6) R.drawable.check_bold else R.drawable.chevron_right
        Image(painterResource(img), "Next")
      }
    }

    if (uiState.sorteoCompleted.isNotEmpty()) {
      MelateSorteoActionDialog(
        { onEvent(CreateFavUiEvent.DismissCreationDialog) },
        { onEvent(CreateFavUiEvent.TapConfirmAdd(uiState.sorteoCompleted)) },
        R.string.txt_sorteo_dialog_title,
        stringResource(R.string.txt_sorteo_dialog_msg, uiState.sorteoCompleted.joinToString()),
        R.string.txt_action_add,
      )
    }
  }
}
