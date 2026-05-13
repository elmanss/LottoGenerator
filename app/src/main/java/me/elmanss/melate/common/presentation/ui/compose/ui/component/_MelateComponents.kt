package me.elmanss.melate.common.presentation.ui.compose.ui.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import me.elmanss.melate.common.presentation.ui.compose.ui.theme.Purple40
import me.elmanss.melate.common.presentation.ui.compose.ui.theme.Purple80
import me.elmanss.melate.common.presentation.ui.compose.ui.theme.White
import me.elmanss.melate.common.util.TestTags

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MelateTopBar(@StringRes title: Int, modifier: Modifier = Modifier) {
  TopAppBar(
    title = { Text(text = stringResource(title)) },
    colors = getTopBarColors(isSystemInDarkTheme()),
    modifier = modifier.testTag(TestTags.SHARED_TOP_BAR),
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MelateActionTopBar(
  @StringRes title: Int,
  modifier: Modifier = Modifier,
  actions: @Composable (RowScope.() -> Unit) = {},
) {
  TopAppBar(
    title = { Text(text = stringResource(title)) },
    colors = getTopBarColors(isSystemInDarkTheme()),
    modifier = modifier.testTag(TestTags.SHARED_TOP_BAR),
    actions = actions,
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
    subtitleContentColor = White,
  )

@Composable
fun MelateFab(
  action: () -> Unit,
  @StringRes text: Int,
  modifier: Modifier = Modifier,
  listState: LazyListState,
) {
  AnimatedVisibility(visible = listState.isScrollingUp().value) {
    FloatingActionButton(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      onClick = { action.invoke() },
      contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
      modifier = modifier.testTag(TestTags.SHARED_FAB_MAIN)
    ) {
      Text(stringResource(text), modifier = Modifier.padding(horizontal = 4.dp))
    }
  }
}

@Composable
fun MelateActionExtendedFab(
  modifier: Modifier = Modifier,
  mainIcon: ImageVector = Icons.Filled.Add,
  mainText: String = "Crear Sorteo",
  listState: LazyListState,
  actionOneIcon: ImageVector = Icons.Filled.ArrowDropDown,
  actionOneText: String = "Descargar",
  onActionOneClicked: () -> Unit,
  actionTwoIcon: ImageVector = Icons.Filled.Create,
  actionTwoText: String = "Crear manualmente",
  onActionTwoClicked: () -> Unit,
) {
  var isExpanded by remember { mutableStateOf(false) }

  val rotationAngle by
    animateFloatAsState(targetValue = if (isExpanded) 45f else 0f, label = "FabRotation")

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.End,
    verticalArrangement = Arrangement.spacedBy(16.dp), // Spacing between FABs
  ) {
    // AnimatedVisibility for the secondary actions
    AnimatedVisibility(
      visible = isExpanded && listState.isScrollingUp().value,
      enter = fadeIn(),
      exit = fadeOut(),
    ) {
      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        FloatingActionButton(
          onClick = {
            onActionOneClicked()
            isExpanded = false // Collapse after action
          },
          containerColor = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
          modifier = Modifier.testTag(TestTags.SHARED_EXTENDED_FAB_ACTION_1)
        ) {
          Icon(imageVector = actionOneIcon, contentDescription = actionOneText)
        }

        FloatingActionButton(
          onClick = {
            onActionTwoClicked()
            isExpanded = false // Collapse after action
          },
          containerColor = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
          modifier = Modifier.testTag(TestTags.SHARED_EXTENDED_FAB_ACTION_2)
        ) {
          Icon(imageVector = actionTwoIcon, contentDescription = actionTwoText)
        }
      }
    }

    AnimatedVisibility(visible = listState.isScrollingUp().value) {

      // Main ExtendedFloatingActionButton
      ExtendedFloatingActionButton(
        onClick = { isExpanded = !isExpanded },
        icon = {
          Icon(
            imageVector = mainIcon,
            contentDescription = mainText,
            modifier = Modifier.rotate(rotationAngle),
          )
        },
        text = { Text(text = mainText) },
        expanded = true, // Keep the main FAB text always visible or control with another state
        modifier = Modifier.testTag(TestTags.SHARED_EXTENDED_FAB_EXPAND)
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
fun MultiActionExtendedFabPreview() {
  MaterialTheme {
    MelateActionExtendedFab(
      modifier = Modifier.padding(16.dp),
      onActionOneClicked = {},
      onActionTwoClicked = {},
      listState = rememberLazyListState(),
    )
  }
}

@Composable
fun LazyListState.isScrollingUp(): State<Boolean> {
  return produceState(initialValue = true) {
    var lastIndex = 0
    var lastScroll = Int.MAX_VALUE
    snapshotFlow { firstVisibleItemIndex to firstVisibleItemScrollOffset }
      .collect { (currentIndex, currentScroll) ->
        if (currentIndex != lastIndex || currentScroll != lastScroll) {
          value =
            currentIndex < lastIndex || (currentIndex == lastIndex && currentScroll < lastScroll)
          lastIndex = currentIndex
          lastScroll = currentScroll
        }
      }
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
          .testTag(TestTags.SHARED_DIALOG_CONTAINER)
          .background(
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(4.dp),
          )
          .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = stringResource(title),
        fontSize = TextUnit(24F, TextUnitType.Sp),
        color = MaterialTheme.colorScheme.onSurface,
      )
      Spacer(modifier.height(8.dp))
      Text(text = msg, color = MaterialTheme.colorScheme.onSurface)
      MelateDialogButton({ action.invoke() }, stringResource(actionTxt), Modifier.testTag(TestTags.SHARED_DIALOG_CONFIRM_BTN))
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
