package me.elmanss.melate.favorites.presentation.create.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import me.elmanss.melate.R
import me.elmanss.melate.common.presentation.ui.compose.ui.component.MelateTopBar
import me.elmanss.melate.favorites.presentation.create.CreateFavoriteScreenViewModel

@Composable
fun CreateFavoriteScreen(viewModel: CreateFavoriteScreenViewModel = hiltViewModel()) {

  Scaffold(topBar = { MelateTopBar(R.string.txt_title_fav_create) }) {
    Column(modifier = Modifier.fillMaxSize().padding(it)) {

    }
  }
}
