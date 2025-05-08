package me.elmanss.melate.common.presentation.ui.compose.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
