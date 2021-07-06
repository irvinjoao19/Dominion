package com.dsige.dominion.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.OtPlazo
import com.dsige.dominion.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_ot_plazo.view.*

class OtPlazoAdapter(private val listener: OnItemClickListener.OtPlazoListener) :
    PagingDataAdapter<OtPlazo, OtPlazoAdapter.ViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = getItem(position)
        if (s != null) {
            holder.bind(s, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_ot_plazo, parent, false)
        return ViewHolder(v)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(p: OtPlazo, listener: OnItemClickListener.OtPlazoListener) =
            with(itemView) {
                textView1.text = p.razonSocial
                textView2.text = String.format("Fuera de Plazo : %s", p.cantidad)
                imgMap.setOnClickListener { v -> listener.onItemClick(p, v, bindingAdapterPosition) }
                imgList.setOnClickListener { v -> listener.onItemClick(p, v, bindingAdapterPosition) }
            }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<OtPlazo>() {
            override fun areItemsTheSame(oldItem: OtPlazo, newItem: OtPlazo): Boolean =
                oldItem.empresaId == newItem.empresaId

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: OtPlazo, newItem: OtPlazo): Boolean =
                oldItem == newItem
        }
    }
}