package me.elmanss.melate.ui.favs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.elmanss.melate.models.FavoritoModel


class FavsAdapter : RecyclerView.Adapter<FavsAdapter.ViewHolder>() {

    private val mItems = mutableListOf<FavoritoModel>()

    fun fill(items: List<FavoritoModel>) {
        if (mItems.isNotEmpty()) mItems.clear()
        mItems.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        mItems.clear()
        notifyDataSetChanged()
    }

    fun getItem(pos: Int): FavoritoModel {
        return mItems[pos]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_selectable_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = mItems[position].sorteo
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(android.R.id.text1)
    }

}