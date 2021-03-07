package me.elmanss.melate.ui.favs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import me.elmanss.melate.databinding.ActivityFavsBinding
import me.elmanss.melate.models.FavoritoModel
import me.elmanss.melate.ui.add.AddToFavActivity


class FavsActivity : AppCompatActivity(), FavsAdapter.DeleteClickListener {
    private lateinit var binding: ActivityFavsBinding
    private val adapter = FavsAdapter()
    private val viewModel: FavsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.addButton.setOnClickListener {
            AddToFavActivity.startForResult(this@FavsActivity)
        }

        binding.favsSorteosView.layoutManager = LinearLayoutManager(this)
        binding.favsSorteosView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // app icon in action bar clicked; go home
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observe() {
        viewModel.favorites.observe(this, {
            it?.let {
                adapter.clear()
                adapter.fill(it)
                viewModel.resetFavorites()
            }
        })
    }


    private fun showWarning(model: FavoritoModel): Boolean {
        AlertDialog.Builder(this)
            .setTitle("Aviso")
            .setMessage("¿Deseas eliminar este sorteo de tu lista de favoritos?")
            .setPositiveButton("Borrar") { d, _ ->
                viewModel.deleteFavs(model)
                d.dismiss()
            }.show()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AddToFavActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                adapter.clear()
                Toast.makeText(this, "Sorteo guardado exitosamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClickDelete(model: FavoritoModel) {
        showWarning(model)
    }
}
