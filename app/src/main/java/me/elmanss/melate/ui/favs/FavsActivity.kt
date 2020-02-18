package me.elmanss.melate.ui.favs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.elmanss.melate.Melate
import me.elmanss.melate.data.FavoritoQueries
import me.elmanss.melate.databinding.ActivityFavsBinding
import me.elmanss.melate.ui.add.AddToFavActivity
import me.elmanss.melate.ui.custom.util.ItemClickSupport


class FavsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavsBinding
    private lateinit var queries: FavoritoQueries
    private val adapter = FavsAdapter()
    private val compositeDisposable = CompositeDisposable()

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
        ItemClickSupport.addTo(binding.favsSorteosView)
            .setOnItemLongClickListener { _, pos, _ ->
                showWarning(pos)
            }

        queries = Melate.get().database.favoritoQueries
        compositeDisposable.add(fillFavs())
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }


    /*
     * return when (item.getItemId()) {
    R.id.home -> {
    // app icon in action bar clicked; go home
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    true
    }
    else -> super.onOptionsItemSelected(item)
    }
     */

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

    private fun fillFavs(): Disposable {


        return queries.selectAll()
            .asObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapToList()
            .subscribe({
                adapter.fill(it)
            }, {
                Log.e("FavsActivity", "Ocurrió un error al obtener favoritos", it)
            })

        /*  return Observable.fromCallable {
              queries.selectAll().executeAsList()
          }.subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe({
                  adapter.fill(it)
              }, {
                  Log.e("FavsActivity", "Ocurrió un error al obtener favoritos", it)
              })*/
    }

    private fun removeFav(pos: Int): Disposable {

        val forDeletion = adapter.getItem(pos)

        return Observable.fromCallable {
            queries.deleteFav(forDeletion.id)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                adapter.removeItem(pos)
                Toast.makeText(
                    this@FavsActivity,
                    "Sorteo eliminado exitosamente",
                    Toast.LENGTH_SHORT
                ).show()
            }, {
                Toast.makeText(
                    this@FavsActivity,
                    "No se pudo eliminar el sorteo",
                    Toast.LENGTH_SHORT
                ).show()
            })
    }

    private fun showWarning(pos: Int): Boolean {
        AlertDialog.Builder(this)
            .setTitle("Aviso")
            .setMessage("¿Deseas eliminar este sorteo de tu lista de favoritos?")
            .setPositiveButton(android.R.string.yes) { d, _ ->
                compositeDisposable.add(removeFav(pos))
                d.dismiss()
            }.show()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AddToFavActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                adapter.clear()
                compositeDisposable.add(fillFavs())
                Toast.makeText(this, "Sorteo guardado exitosamente", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
