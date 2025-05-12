package me.elmanss.melate.favorites.presentation.create.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import me.elmanss.melate.R
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateTopBar
import me.elmanss.melate.common.presentation.ui.compose.ui.theme.melateRed
import me.elmanss.melate.favorites.presentation.create.CreateFavoriteScreenViewModel

@Composable
fun CreateFavoriteScreen(viewModel: CreateFavoriteScreenViewModel = hiltViewModel()) {

  Scaffold(topBar = { MelateTopBar(R.string.txt_title_fav_create) }) {
    ConstraintLayout(modifier = Modifier.padding(it).fillMaxSize()) {
      val (
        largeText,
        statutText,
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

      // 1st-row
      TextButton(
        onClick = {},
        modifier =
          Modifier.constrainAs(one) {
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
        onClick = {},
        modifier =
          Modifier.constrainAs(two) {
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
        onClick = {},
        modifier =
          Modifier.constrainAs(three) {
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
        onClick = {},
        modifier =
          Modifier.constrainAs(four) {
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
        onClick = {},
        modifier =
          Modifier.constrainAs(five) {
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
        onClick = {},
        modifier =
          Modifier.constrainAs(six) {
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
        onClick = {},
        modifier =
          Modifier.constrainAs(seven) {
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
        onClick = {},
        modifier =
          Modifier.constrainAs(eight) {
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
        onClick = {},
        modifier =
          Modifier.constrainAs(nine) {
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
        onClick = {},
        modifier =
          Modifier.constrainAs(backspace) {
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(zero.start)
          },
      ) {
        Image(painterResource(R.drawable.backspace), "Backspace")
      }

      TextButton(
        onClick = {},
        modifier =
          Modifier.constrainAs(zero) {
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
        onClick = {},
        modifier =
          Modifier.constrainAs(ok) {
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end)
            start.linkTo(zero.end)
          },
      ) {
        Text(
          text = stringResource(R.string.label_keyboard_OK),
          color = melateRed(),
          textAlign = TextAlign.Center,
        )
      }
    }
  }
}
