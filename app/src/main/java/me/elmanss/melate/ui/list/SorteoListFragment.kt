package me.elmanss.melate.ui.list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import logcat.logcat
import me.elmanss.melate.R
import me.elmanss.melate.databinding.FragmentSorteoListBinding
import me.elmanss.melate.ui.custom.util.ItemClickSupport

class SorteoListFragment : Fragment(R.layout.fragment_sorteo_list),
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentSorteoListBinding
    private val adapter = SorteoListAdapter()
    private val viewModel: SorteoListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSorteoListBinding.bind(view)
        configBinding(binding)

        Toast.makeText(
            view.context,
            "Para agregar sorteos a tus favoritos, mantén presionado el sorteo de tu elección",
            Toast.LENGTH_LONG
        ).show()

        observe()
    }

    private fun configBinding(binding: FragmentSorteoListBinding) {
        binding.apply {
            mainScreen.setOnRefreshListener(this@SorteoListFragment)
            mainSorteosView.layoutManager = LinearLayoutManager(mainSorteosView.context)
            mainSorteosView.addItemDecoration(
                DividerItemDecoration(
                    mainSorteosView.context, LinearLayoutManager.VERTICAL
                )
            )

            mainSorteosView.adapter = adapter
            ItemClickSupport.addTo(mainSorteosView).setOnItemClickListener { _, pos, _ ->
                showWarning(pos)
            }

            bMainFavs.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(R.id.action_sorteoListFragment_to_favsFragment)
            }
        }
    }

    private fun observe() {
        viewModel.sorteos.observe(viewLifecycleOwner) {
            it?.let {
                adapter.add(it)
                logcat { "Added item ${it.prettyPrint()} at position ${adapter.itemCount - 1}" }
            }
        }
    }

    private fun showWarning(pos: Int) {
        context?.let { c ->
            AlertDialog.Builder(c).setTitle("Aviso")
                .setMessage("¿Deseas agregar este sorteo de tu lista de favoritos?")
                .setPositiveButton("Si") { d, _ ->
                    viewModel.saveToFavorites(adapter.getItem(pos))
                    Toast.makeText(
                        c, "Sorteo agregado a tus favoritos", Toast.LENGTH_SHORT
                    ).show()
                    d.dismiss()
                }.show()
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
            viewModel.fetchSorteos()
        }
    }
}