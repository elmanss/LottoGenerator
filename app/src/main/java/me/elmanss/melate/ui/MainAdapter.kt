package me.elmanss.melate.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


/**
 * ADDCEL on 2/5/19.
 */
class MainAdapter : RecyclerView.Adapter<ViewHolder>() {

    private val items = ArrayList<List<Int>>()

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun add(item: List<Int>, pos: Int) {
        items.add(item)
        notifyItemInserted(pos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_selectable_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.textView.text = items[position].toString()

        /*
    RxView.clicks(viewHolder.itemView) //viewHolder.itemView here You have access to view
            .map(aVoid -> viewHolder.getPlace())
            .subscribe(itemViewClickSubject);
         */


        /*

    RxView.clicks(holder.textView)
            .takeUntil(RxView.detaches(pParent))
            .map(aVoid -> view)
            .subscribe(mViewClickSubject);
         */
    }

    fun getItem(pos: Int): List<Int> {
        return items[pos]
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(android.R.id.text1)
}