package me.elmanss.melate.favorites.presentation.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.logcat
import me.elmanss.melate.R
import me.elmanss.melate.databinding.FragmentFavsBinding
import me.elmanss.melate.favorites.domain.model.FavoritoModel

@AndroidEntryPoint

class ListFavoritesScreenFragment :
  Fragment(R.layout.fragment_favs), ListFavoritesAdapter.DeleteClickListener {
  private lateinit var binding: FragmentFavsBinding
  private val adapter = ListFavoritesAdapter()
  private val viewModel: ListFavoritesScreenViewModel by viewModels<ListFavoritesScreenViewModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding = FragmentFavsBinding.bind(view)

    binding.addButton.setOnClickListener {
      Navigation.findNavController(it)
        .navigate(ListFavoritesScreenFragmentDirections.actionFavsFragmentToAddToFavFragment())
    }

    binding.favsSorteosView.layoutManager = LinearLayoutManager(view.context)
    binding.favsSorteosView.addItemDecoration(
      DividerItemDecoration(view.context, LinearLayoutManager.VERTICAL)
    )

    binding.favsSorteosView.adapter = adapter
    observe()
  }

  override fun onResume() {
    super.onResume()
    adapter.setListener(this)
  }

  override fun onPause() {
    super.onPause()
    adapter.removeListener()
  }

  private fun observe() {
    lifecycleScope.launch {
      viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collectLatest {
        if (it.favs.isNotEmpty()) {
          adapter.fill(it.favs)
        } else {
          logcat { "No favs available" }
        }
      }
    }
  }

  private fun showWarning(model: FavoritoModel): Boolean {
    context?.let { c ->
      AlertDialog.Builder(c)
        .setTitle("Aviso")
        .setMessage("¿Deseas eliminar este sorteo de tu lista de favoritos?")
        .setPositiveButton("Borrar") { d, _ ->
          viewModel.deleteFavs(model)
          d.dismiss()
        }
        .show()

      return true
    }

    return false
  }

  override fun onClickDelete(model: FavoritoModel) {
    showWarning(model)
  }
}
