package me.elmanss.melate.home.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.logcat
import me.elmanss.melate.R
import me.elmanss.melate.databinding.FragmentSorteoListBinding

@AndroidEntryPoint
class HomeScreenFragment :
  Fragment(R.layout.fragment_sorteo_list), SwipeRefreshLayout.OnRefreshListener {

  private lateinit var binding: FragmentSorteoListBinding
  private val adapter = HomeListAdapter { onItemClicked(it) }
  private val viewModel: HomeScreenViewModel by viewModels<HomeScreenViewModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding = FragmentSorteoListBinding.bind(view)
    configBinding(binding)
    observe()

    Toast.makeText(
        view.context,
        "Para agregar sorteos a tus favoritos, mantén presionado el sorteo de tu elección",
        Toast.LENGTH_LONG,
      )
      .show()
  }

  private fun onItemClicked(pos: Int) {
    if (binding.root.isRefreshing) {
      logcat { "Sorteos are being refreshed" }
    } else {
      showWarning(pos).also { viewModel.sendEvent(HomeUiEvent.HideSaveSorteoDialog) }
    }
  }

  private fun configBinding(binding: FragmentSorteoListBinding) {
    binding.apply {
      mainScreen.setOnRefreshListener(this@HomeScreenFragment)
      mainSorteosView.layoutManager = LinearLayoutManager(mainSorteosView.context)
      mainSorteosView.addItemDecoration(
        DividerItemDecoration(mainSorteosView.context, LinearLayoutManager.VERTICAL)
      )

      mainSorteosView.adapter = adapter
      bMainFavs.setOnClickListener {
        it
          .findNavController()
          .navigate(HomeScreenFragmentDirections.actionSorteoListFragmentToFavsFragment())
      }
    }
  }

  private fun observe() {
    lifecycleScope.launch {
      viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collectLatest {
        if (it.sorteos.isNotEmpty()) {
          it.sorteos.forEach {
            if (adapter.containsItem(it)) {
              logcat { "item ${it.prettyPrint()} is already in list" }
            } else {
              adapter.add(it)
              logcat { "Added item ${it.prettyPrint()} at position ${adapter.itemCount - 1}" }
            }
          }
        } else {
          logcat { "Could not retrieve sorteos" }
        }
      }
    }
  }

  private fun showWarning(pos: Int) {
    context?.let { c ->
      AlertDialog.Builder(c)
        .setTitle(getString(R.string.txt_title_aviso))
        .setMessage(getString(R.string.txt_msg_add_to_fav))
        .setPositiveButton(getString(R.string.txt_action_yes)) { d, _ ->
          viewModel.sendEvent(HomeUiEvent.ConfirmSaveSorteo(adapter.getItem(pos)))
          viewModel.sendEvent(HomeUiEvent.HideSaveSorteoDialog)
          Toast.makeText(c, "Sorteo agregado a tus favoritos", Toast.LENGTH_SHORT).show()
          d.dismiss()
        }
        .setOnDismissListener { viewModel.sendEvent(HomeUiEvent.HideSaveSorteoDialog) }
        .setOnCancelListener { viewModel.sendEvent(HomeUiEvent.HideSaveSorteoDialog) }
        .show()
    }
  }

  override fun onRefresh() {
    1500L.launchOnRefresh()
  }

  private fun Long.launchOnRefresh() {
    lifecycleScope.launch {
      adapter.clear()
      delay(this@launchOnRefresh)
      binding.root.isRefreshing = false
      viewModel.sendEvent(HomeUiEvent.RefreshSorteos)
    }
  }
}
