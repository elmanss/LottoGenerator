package me.elmanss.melate.favorites.presentation.create.compose

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
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import logcat.logcat
import me.elmanss.melate.R
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateSorteoActionDialog
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateTopBar
import me.elmanss.melate.common.presentation.ui.compose.ui.theme.melateRed
import me.elmanss.melate.favorites.presentation.create.CreateFavUiEvent
import me.elmanss.melate.favorites.presentation.create.CreateFavoriteScreenViewModel

@Composable
fun CreateFavoriteScreen(viewModel: CreateFavoriteScreenViewModel = hiltViewModel()) {

  val uiState = viewModel.state.collectAsState()
  val snackbarState = remember { SnackbarHostState() }

  val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

  Scaffold(
    topBar = { MelateTopBar(R.string.txt_title_fav_create) },
    snackbarHost = { SnackbarHost(snackbarState) },
  ) {
    ConstraintLayout(modifier = Modifier.padding(it).fillMaxSize()) {
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
        uiState.value.keyboardInput.let {
          Text(
            it,
            fontSize = dimensionResource(R.dimen.key_number_font_size).value.sp,
            color = melateRed(),
          )
        }
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
        logcat { "Added: ${uiState.value.numbers.joinToString()}" }
        if (uiState.value.numbers.isNotEmpty()) {
          Text(
            modifier = Modifier.wrapContentHeight().fillMaxWidth(),
            text = uiState.value.numbers.joinToString(),
            color = melateRed(),
            fontSize = TextUnit(20F, TextUnitType.Sp),
            textAlign = TextAlign.Center,
          )
        }
      }

      // 1st-row
      TextButton(
        onClick = { viewModel.sendEvent(CreateFavUiEvent.TapDigit("1")) },
        modifier =
          Modifier.height(dimensionResource(R.dimen.key_size)).constrainAs(one) {
            bottom.linkTo(four.top)
            start.linkTo(parent.start)
            end.linkTo(two.start)
          },
      ) {
        Text(
          text = stringResource(R.string.label_keyboard_1),
          color = melateRed(),
          textAlign = TextAlign.Center,
        )
      }

      TextButton(
        onClick = { viewModel.sendEvent(CreateFavUiEvent.TapDigit("2")) },
        modifier =
          Modifier.height(dimensionResource(R.dimen.key_size)).constrainAs(two) {
            bottom.linkTo(five.top)
            start.linkTo(one.end)
            end.linkTo(three.start)
          },
      ) {
        Text(
          text = stringResource(R.string.label_keyboard_2),
          color = melateRed(),
          textAlign = TextAlign.Center,
        )
      }

      TextButton(
        onClick = { viewModel.sendEvent(CreateFavUiEvent.TapDigit("3")) },
        modifier =
          Modifier.height(dimensionResource(R.dimen.key_size)).constrainAs(three) {
            bottom.linkTo(six.top)
            end.linkTo(parent.end)
            start.linkTo(two.end)
          },
      ) {
        Text(
          text = stringResource(R.string.label_keyboard_3),
          color = melateRed(),
          textAlign = TextAlign.Center,
        )
      }

      // 2nd-row
      TextButton(
        onClick = { viewModel.sendEvent(CreateFavUiEvent.TapDigit("4")) },
        modifier =
          Modifier.height(dimensionResource(R.dimen.key_size)).constrainAs(four) {
            bottom.linkTo(seven.top)
            start.linkTo(parent.start)
            end.linkTo(five.start)
          },
      ) {
        Text(
          text = stringResource(R.string.label_keyboard_4),
          color = melateRed(),
          textAlign = TextAlign.Center,
        )
      }

      TextButton(
        onClick = { viewModel.sendEvent(CreateFavUiEvent.TapDigit("5")) },
        modifier =
          Modifier.height(dimensionResource(R.dimen.key_size)).constrainAs(five) {
            bottom.linkTo(eight.top)
            start.linkTo(four.end)
            end.linkTo(six.start)
          },
      ) {
        Text(
          text = stringResource(R.string.label_keyboard_5),
          color = melateRed(),
          textAlign = TextAlign.Center,
        )
      }

      TextButton(
        onClick = { viewModel.sendEvent(CreateFavUiEvent.TapDigit("6")) },
        modifier =
          Modifier.height(dimensionResource(R.dimen.key_size)).constrainAs(six) {
            bottom.linkTo(nine.top)
            end.linkTo(parent.end)
            start.linkTo(five.end)
          },
      ) {
        Text(
          text = stringResource(R.string.label_keyboard_6),
          color = melateRed(),
          textAlign = TextAlign.Center,
        )
      }

      // 3rd-row
      TextButton(
        onClick = { viewModel.sendEvent(CreateFavUiEvent.TapDigit("7")) },
        modifier =
          Modifier.height(dimensionResource(R.dimen.key_size)).constrainAs(seven) {
            bottom.linkTo(backspace.top)
            start.linkTo(parent.start)
            end.linkTo(eight.start)
          },
      ) {
        Text(
          text = stringResource(R.string.label_keyboard_7),
          color = melateRed(),
          textAlign = TextAlign.Center,
        )
      }

      TextButton(
        onClick = { viewModel.sendEvent(CreateFavUiEvent.TapDigit("8")) },
        modifier =
          Modifier.height(dimensionResource(R.dimen.key_size)).constrainAs(eight) {
            bottom.linkTo(zero.top)
            start.linkTo(seven.end)
            end.linkTo(nine.start)
          },
      ) {
        Text(
          text = stringResource(R.string.label_keyboard_8),
          color = melateRed(),
          textAlign = TextAlign.Center,
        )
      }

      TextButton(
        onClick = { viewModel.sendEvent(CreateFavUiEvent.TapDigit("9")) },
        modifier =
          Modifier.height(dimensionResource(R.dimen.key_size)).constrainAs(nine) {
            bottom.linkTo(ok.top)
            end.linkTo(parent.end)
            start.linkTo(eight.end)
          },
      ) {
        Text(
          text = stringResource(R.string.label_keyboard_9),
          color = melateRed(),
          textAlign = TextAlign.Center,
        )
      }

      // Bottom-row
      TextButton(
        onClick = { viewModel.sendEvent(CreateFavUiEvent.TapDelete) },
        modifier =
          Modifier.height(dimensionResource(R.dimen.key_size)).constrainAs(backspace) {
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(zero.start)
          },
      ) {
        Image(painterResource(R.drawable.backspace), "Backspace")
      }

      TextButton(
        onClick = { viewModel.sendEvent(CreateFavUiEvent.TapDigit("0")) },
        modifier =
          Modifier.height(dimensionResource(R.dimen.key_size)).constrainAs(zero) {
            bottom.linkTo(parent.bottom)
            start.linkTo(backspace.end)
            end.linkTo(ok.start)
          },
      ) {
        Text(
          text = stringResource(R.string.label_keyboard_0),
          color = melateRed(),
          textAlign = TextAlign.Center,
        )
      }

      TextButton(
        onClick = { viewModel.sendEvent(CreateFavUiEvent.TapNext) },
        modifier =
          Modifier.height(dimensionResource(R.dimen.key_size)).constrainAs(ok) {
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end)
            start.linkTo(zero.end)
          },
      ) {
        val img =
          if (uiState.value.numbers.size == 6) R.drawable.check_bold else R.drawable.chevron_right
        Image(painterResource(img), "Next")
      }
    }

    if (uiState.value.sorteoCompleted.isNotEmpty()) {
      MelateSorteoActionDialog(
        { viewModel.clearSorteoCompleted() },
        { viewModel.sendEvent(CreateFavUiEvent.InsertFavorite(uiState.value.sorteoCompleted)) },
        R.string.txt_sorteo_dialog_title,
        stringResource(R.string.txt_sorteo_dialog_msg, uiState.value.sorteoCompleted),
        R.string.txt_action_add,
      )
    }

    if (uiState.value.captureError.isNotEmpty()) {
      LaunchedEffect(true) {
        val result =
          snackbarState.showSnackbar(
            message = uiState.value.captureError,
            duration = SnackbarDuration.Short,
          )
        when (result) {
          SnackbarResult.Dismissed -> {
            viewModel.clearError()
          }

          SnackbarResult.ActionPerformed -> {}
        }
      }
    }

    if (uiState.value.sorteoStored) {
      val msg = stringResource(R.string.txt_sorteo_success)
      LaunchedEffect(true) {
        val result = snackbarState.showSnackbar(message = msg, duration = SnackbarDuration.Short)
        when (result) {
          SnackbarResult.Dismissed -> {
            viewModel.showMessage(false)
          }

          SnackbarResult.ActionPerformed -> {}
        }
      }
    }

    if (uiState.value.navigateBack) {
      onBackPressedDispatcher?.onBackPressed()
      viewModel.clearBackNavigation()
    }

    if (uiState.value.sorteoInserted) {
      viewModel.clearAfterStorage()
    }
  }
}
