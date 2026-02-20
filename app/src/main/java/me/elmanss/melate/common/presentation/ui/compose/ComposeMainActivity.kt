package me.elmanss.melate.common.presentation.ui.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import dagger.hilt.android.AndroidEntryPoint
import me.elmanss.melate.common.presentation.ui.compose.ui.navigation.LottoGeneratorNavDisplay
import me.elmanss.melate.common.presentation.ui.compose.ui.navigation.Screen
import me.elmanss.melate.common.presentation.ui.compose.ui.theme.LottoGeneratorTheme

@AndroidEntryPoint
class ComposeMainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      LottoGeneratorTheme {
        val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }
        LottoGeneratorNavDisplay(backStack)
      }
    }
  }
}
