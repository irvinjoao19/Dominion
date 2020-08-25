package com.dsige.dominion.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.Distrito
import com.dsige.dominion.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_combo.view.*
import java.util.*
import kotlin.collections.ArrayList

class DistritoAdapter(private val listener: OnItemClickListener.DistritoListener) :
    RecyclerView.Adapter<DistritoAdapter.ViewHolder>() {

    private var d = emptyList<Distrito>()
    private var dList: ArrayList<Distrito> = ArrayList()

    fun addItems(list: List<Distrito>) {
        d = list
        dList = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_combo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dList[position], listener)
    }

    override fun getItemCount(): Int {
        return dList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(m: Distrito, listener: OnItemClickListener.DistritoListener) = with(itemView) {
            textViewTitulo.text = m.nombreDistrito
            itemView.setOnClickListener { v -> listener.onItemClick(m, v, adapterPosition) }
        }
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                return FilterResults()
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                dList.clear()
                val keyword = charSequence.toString()
                if (keyword.isEmpty()) {
                    dList.addAll(d)
                } else {
                    val filteredList = ArrayList<Distrito>()
                    for (s: Distrito in d) {
                        if (s.nombreDistrito.toLowerCase(
                                Locale.getDefault()).contains(
                                keyword
                            )
                        ) {
                            filteredList.add(s)
                        }
                    }
                    dList = filteredList
                }
                notifyDataSetChanged()
            }
        }
    }
}