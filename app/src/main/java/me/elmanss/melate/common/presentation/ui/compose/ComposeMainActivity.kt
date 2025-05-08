package me.elmanss.melate.common.presentation.ui.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import me.elmanss.melate.common.presentation.ui.compose.ui.theme.LottoGeneratorTheme
import me.elmanss.melate.home.presentation.compose.HomeScreen

@AndroidEntryPoint
class ComposeMainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      LottoGeneratorTheme {
        //        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        //          Greeting(name = "Android", modifier = Modifier.padding(innerPadding))
        //        }
          HomeScreen()
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  LottoGeneratorTheme { Greeting("Android") }
}
