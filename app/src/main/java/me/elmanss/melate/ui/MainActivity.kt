package me.elmanss.melate.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import me.elmanss.melate.Pool
import me.elmanss.melate.R
import me.elmanss.melate.databinding.ActivityMainBinding
import me.elmanss.melate.ui.custom.util.ItemClickSupport
import me.elmanss.melate.ui.favs.FavsActivity
import timber.log.Timber

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
        ItemClickSupport.addTo(binding.mainSorteosView).setOnItemClickListener { _, pos, _ ->
            showWarning(pos)
        }

        Toast.makeText(
            this,
            "Presiona sobre un sorteo para guardarlo en tu dispositivo.",
            Toast.LENGTH_LONG
        ).show()

        observe()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_favs -> startActivity(Intent(this, FavsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observe() {
        viewModel.sorteos.observe(this, {
            it?.let {
                adapter.add(it)
                Timber.d("Added item ${it.prettyPrint()} at position ${adapter.itemCount - 1}")
            }
        })
    }

    private fun showWarning(pos: Int) {
        AlertDialog.Builder(this)
            .setTitle("Aviso")
            .setMessage("¿Deseas guardar este sorteo?")
            .setPositiveButton("Si") { d, _ ->
                viewModel.saveToFavorites(adapter.getItem(pos))
                Toast.makeText(
                    this@MainActivity,
                    "Se ha guardado este sorteo en tu dispositivo",
                    Toast.LENGTH_SHORT
                )
                    .show()
                d.dismiss()
            }.show()
    }

    override fun onRefresh() {
        1500L.launchOnRefresh()
    }

    private fun Long.launchOnRefresh() {
        lifecycleScope.launch {
            adapter.clear()
            delay(this@launchOnRefresh)
            binding.root.isRefreshing = false
            viewModel.fetchSorteos(Pool.numbers)
        }
    }
}
