package me.elmanss.melate.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.disposables.CompositeDisposable
import me.elmanss.melate.Melate
import me.elmanss.melate.databinding.ActivityMainBinding
import me.elmanss.melate.ui.custom.util.ItemClickSupport
import me.elmanss.melate.ui.favs.FavsActivity
import java.util.concurrent.ThreadLocalRandom

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    lateinit var adapter: MainAdapter
    override fun onRefresh() {
        Handler().postDelayed({
            fillView()
            binding.mainScreen.isRefreshing = false
        }, 1500)
    }

    private lateinit var presenter: MainPresenter
    private val compositeDisposable = CompositeDisposable()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)
        presenter =
            MainPresenter(ThreadLocalRandom.current(), Melate.get().database.favoritoQueries)
        binding.mainScreen.setOnRefreshListener(this)
        adapter = MainAdapter()
        binding.mainSorteosView.layoutManager = LinearLayoutManager(this)
        binding.mainSorteosView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        binding.mainSorteosView.adapter = adapter
        ItemClickSupport.addTo(binding.mainSorteosView).setOnItemLongClickListener { _, pos, _ ->
            showWarning(pos)
        }
        if (adapter.itemCount == 0)
            fillView()

        binding.favsButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, FavsActivity::class.java))
        }


        Toast.makeText(
            this,
            "Para agregar sorteos a tus favoritos, mantén presionado el sorteo de tu elección",
            Toast.LENGTH_LONG
        ).show()

    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun fillView() {
        adapter.clear()

        val sorteos = presenter.multiSorteo()

        for (i in sorteos.indices) {
            adapter.add(sorteos[i], i)
        }

    }

    private fun showWarning(pos: Int): Boolean {
        AlertDialog.Builder(this)
            .setTitle("Aviso")
            .setMessage("¿Deseas agregar este sorteo de tu lista de favoritos?")
            .setPositiveButton(android.R.string.yes) { d, _ ->
                compositeDisposable.add(presenter.saveToFavs(adapter.getItem(pos).toString()))
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
}
