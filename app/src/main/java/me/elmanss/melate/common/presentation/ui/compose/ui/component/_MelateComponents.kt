package me.elmanss.melate.common.presentation.ui.compose.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import me.elmanss.melate.common.presentation.ui.compose.ui.theme.Purple40
import me.elmanss.melate.common.presentation.ui.compose.ui.theme.Purple80
import me.elmanss.melate.common.presentation.ui.compose.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MelateTopBar(@StringRes title: Int, modifier: Modifier = Modifier) {
  TopAppBar(
    title = { Text(text = stringResource(title)) },
    colors = getTopBarColors(isSystemInDarkTheme()),
    modifier = modifier,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
private fun getTopBarColors(isDark: Boolean) =
  TopAppBarColors(
    containerColor = if (isDark) Purple40 else Purple80,
    scrolledContainerColor = if (isDark) Purple40 else Purple80,
    navigationIconContentColor = White,
    titleContentColor = White,
    actionIconContentColor = White,
  )

@Composable
fun MelateFab(action: () -> Unit, @StringRes text: Int, modifier: Modifier = Modifier) {
  FloatingActionButton(
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    onClick = { action.invoke() },
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
  ) {
    Text(stringResource(text), modifier = modifier.padding(horizontal = 4.dp))
  }
}

@Composable
fun MelateSorteoActionDialog(
  onDismiss: () -> Unit,
  action: () -> Unit,
  @StringRes title: Int,
  @StringRes msg: Int,
  @StringRes actionTxt: Int,
  modifier: Modifier = Modifier,
) {
  MelateSorteoActionDialog(onDismiss, action, title, stringResource(msg), actionTxt, modifier)
}

@Composable
fun MelateSorteoActionDialog(
  onDismiss: () -> Unit,
  action: () -> Unit,
  @StringRes title: Int,
  msg: String,
  @StringRes actionTxt: Int,
  modifier: Modifier = Modifier,
) {
  Dialog({ onDismiss.invoke() }, properties = DialogProperties()) {
    Column(
      modifier =
        modifier
          .background(
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(4.dp),
          )
          .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(text = stringResource(title), fontSize = TextUnit(24F, TextUnitType.Sp))
      Spacer(modifier.height(8.dp))
      Text(text = msg)
      MelateDialogButton({ action.invoke() }, stringResource(actionTxt))
    }
  }
}

@Composable
fun MelateDialogButton(action: () -> Unit, text: String, modifier: Modifier = Modifier) {
  val colors =
    ButtonColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
      disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
      disabledContainerColor = MaterialTheme.colorScheme.inversePrimary,
    )
  TextButton(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    colors = colors,
    onClick = { action.invoke() },
  ) {
    Text(text)
  }
}
