package me.elmanss.melate.ui.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import me.elmanss.melate.Melate
import me.elmanss.melate.data.FavoritoQueries
import me.elmanss.melate.data.Sorteo
import me.elmanss.melate.databinding.ActivityAddToFavBinding

class AddToFavActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CODE = 200
        fun startForResult(context: Context) {
            ((context as Activity).startActivityForResult(
                Intent(
                    context,
                    AddToFavActivity::class.java
                ), REQUEST_CODE
            ))
        }
    }

    private lateinit var binding: ActivityAddToFavBinding
    private lateinit var queries: FavoritoQueries

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddToFavBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        queries = Melate.get().database.favoritoQueries
        binding.bAddSave.setOnClickListener {
            clickSave()
        }
    }

    private fun fetchValues(): List<Int> {
        return listOf(
            (binding.spinner1.selectedItem as String).toInt(),
            (binding.spinner2.selectedItem as String).toInt(),
            (binding.spinner3.selectedItem as String).toInt(),
            (binding.spinner4.selectedItem as String).toInt(),
            (binding.spinner5.selectedItem as String).toInt(),
            (binding.spinner6.selectedItem as String).toInt()
        ).sorted()
    }

    private fun hasDuplicates(sorteo: List<Int>): Boolean {
        return sorteo.size != sorteo.distinct().size
    }

    private fun clickSave() {
        val sorteo = fetchValues()
        if (hasDuplicates(sorteo)) {
            Toast.makeText(this, "No debe haber números repetidos", Toast.LENGTH_SHORT).show()
        } else {
            AlertDialog.Builder(this).setTitle("Números seleccionados").setMessage(
                "Los números que seleccionaste son: \n$sorteo.\n\n ¿Deseas guardarlos?"
            ).setPositiveButton("Guardar") { _, _ ->
                saveToFavs(Sorteo(sorteo))
            }.setNegativeButton("Cancelar") { d, _ -> d.dismiss() }.show()
        }
    }

    private fun saveToFavs(sorteo: Sorteo) {
        queries.insertFav(sorteo.prettyPrint())
        Log.i("MainPresenter", "Favorito agregado con exito")
        setResult(Activity.RESULT_OK)
        finish()
    }
}
