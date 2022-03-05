package me.elmanss.melate.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.elmanss.melate.models.SorteoModel


class SorteoListAdapter : RecyclerView.Adapter<SorteoListAdapter.ViewHolder>() {
    private val items = mutableListOf<SorteoModel>()

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun containsItem(item: SorteoModel): Boolean {
        return item in items
    }

    fun add(item: SorteoModel) {
        items.add(item)
        notifyItemInserted(items.lastIndex)
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