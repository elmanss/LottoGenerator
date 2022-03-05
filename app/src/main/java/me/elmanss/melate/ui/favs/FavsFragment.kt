package me.elmanss.melate.ui.favs

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import me.elmanss.melate.R
import me.elmanss.melate.databinding.FragmentFavsBinding
import me.elmanss.melate.models.FavoritoModel

class FavsFragment : Fragment(R.layout.fragment_favs), FavsAdapter.DeleteClickListener {
    private lateinit var binding: FragmentFavsBinding
    private val adapter = FavsAdapter()
    private val viewModel: FavsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavsBinding.bind(view)

        binding.addButton.setOnClickListener {
//            AddToFavActivity.startForResult(this@FavsActivity)
            Navigation.findNavController(it).navigate(R.id.action_favsFragment_to_addToFavFragment)
        }

        binding.favsSorteosView.layoutManager = LinearLayoutManager(view.context)
        binding.favsSorteosView.addItemDecoration(
            DividerItemDecoration(
                view.context,
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

    //
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                // app icon in action bar clicked; go home
//                onBackPressed()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//
    private fun observe() {
        viewModel.favorites.observe(viewLifecycleOwner) {
            it?.let {
                adapter.clear()
                adapter.fill(it)
                viewModel.resetFavorites()
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
                }.show()

            return true
        }

        return false
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == AddToFavActivity.REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                adapter.clear()
//                Toast.makeText(this, "Sorteo guardado exitosamente", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }


    override fun onClickDelete(model: FavoritoModel) {
        showWarning(model)
    }
}