package me.elmanss.melate.favorites.presentation.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.elmanss.melate.R
import me.elmanss.melate.favorites.domain.model.FavoritoModel

class ListFavoritesAdapter : RecyclerView.Adapter<ListFavoritesAdapter.ViewHolder>() {
  private val _items = mutableListOf<FavoritoModel>()
  private var _listener: DeleteClickListener? = null

  fun setListener(listener: DeleteClickListener) {
    _listener = listener
  }

  fun removeListener() {
    _listener = null
  }

  fun fill(items: List<FavoritoModel>) {
    if (_items.isNotEmpty()) _items.clear()
    _items.addAll(items)
    notifyDataSetChanged()
  }

  fun clear() {
    _items.clear()
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_melate_fav, parent, false)
    return ViewHolder(view)
  }

  override fun getItemCount(): Int {
    return _items.size
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val fav = _items[position]
    holder.textView.text = fav.sorteo
    holder.deleteButton.setOnClickListener { _listener?.onClickDelete(fav) }
  }

  class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(R.id.text1)
    var deleteButton: ImageButton = view.findViewById(R.id.ic_delete)
  }

  interface DeleteClickListener {
    fun onClickDelete(model: FavoritoModel)
  }
}
