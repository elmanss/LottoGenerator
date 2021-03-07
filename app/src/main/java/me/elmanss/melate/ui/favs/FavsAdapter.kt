package me.elmanss.melate.ui.favs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.elmanss.melate.R
import me.elmanss.melate.models.FavoritoModel


class FavsAdapter : RecyclerView.Adapter<FavsAdapter.ViewHolder>() {
    //    'm' prefix stands for mutable, not ugly-ass hungarian notation
    private val mItems = mutableListOf<FavoritoModel>()
    private var mListener: DeleteClickListener? = null

    fun setListener(listener: DeleteClickListener) {
        mListener = listener
    }

    fun removeListener() {
        mListener = null
    }

    fun fill(items: List<FavoritoModel>) {
        if (mItems.isNotEmpty()) mItems.clear()
        mItems.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        mItems.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_melate_fav, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fav = mItems[position]
        holder.textView.text = fav.sorteo
        holder.deleteButton.setOnClickListener {
            mListener?.onClickDelete(fav)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text1)
        var deleteButton: ImageButton = view.findViewById(R.id.ic_delete)
    }

    interface DeleteClickListener {
        fun onClickDelete(model: FavoritoModel)
    }

}