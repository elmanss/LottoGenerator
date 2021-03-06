package me.elmanss.melate.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.elmanss.melate.data.Sorteo


class MainAdapter : RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    private val items = mutableListOf<Sorteo>()

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun add(item: Sorteo, pos: Int) {
        items.add(item)
        notifyItemInserted(pos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_selectable_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position].prettyPrint()
    }

    fun getItem(pos: Int) = items[pos]

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(android.R.id.text1)
    }
}