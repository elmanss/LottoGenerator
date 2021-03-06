package me.elmanss.melate.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.elmanss.melate.databinding.ActivityMainBinding
import me.elmanss.melate.ui.custom.util.ItemClickSupport
import me.elmanss.melate.ui.favs.FavsActivity

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private val adapter = MainAdapter()
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)
        binding.mainScreen.setOnRefreshListener(this)
        binding.mainSorteosView.layoutManager = LinearLayoutManager(this)
        binding.mainSorteosView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

        binding.mainSorteosView.adapter = adapter
        ItemClickSupport.addTo(binding.mainSorteosView).setOnItemLongClickListener { _, pos, _ ->
            showWarning(pos)
        }

        binding.favsButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, FavsActivity::class.java))
        }

        Toast.makeText(
            this,
            "Para agregar sorteos a tus favoritos, mantén presionado el sorteo de tu elección",
            Toast.LENGTH_LONG
        ).show()

        observe()
        viewModel.multiSorteo()
    }

    private fun observe() {
        viewModel.sorteos.observe(this, {
            it?.let {
                adapter.clear()
                it.forEachIndexed { index, sorteo -> adapter.add(sorteo, index) }
                viewModel.resetSorteos()
            }
        })
    }

    private fun showWarning(pos: Int): Boolean {
        AlertDialog.Builder(this)
            .setTitle("Aviso")
            .setMessage("¿Deseas agregar este sorteo de tu lista de favoritos?")
            .setPositiveButton("Si") { d, _ ->
                viewModel.saveToFavorites(adapter.getItem(pos))
                Toast.makeText(
                    this@MainActivity,
                    "Sorteo agregado a tus favoritos",
                    Toast.LENGTH_SHORT
                )
                    .show()
                d.dismiss()
            }.show()
        return true
    }

    override fun onRefresh() {
        1500L.launchOnRefresh()
    }

    private fun Long.launchOnRefresh() {
        lifecycleScope.launch {
            delay(this@launchOnRefresh)
            binding.root.isRefreshing = false
            viewModel.multiSorteo()
        }
    }
}
