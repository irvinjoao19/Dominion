package com.dsige.dominion.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.Proveedor
import com.dsige.dominion.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_proveedor.view.*

class ProveedorAdapter(private val listener: OnItemClickListener.ProveedorListener) :
    PagingDataAdapter<Proveedor, ProveedorAdapter.ViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = getItem(position)
        if (s != null) {
            holder.bind(s, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_proveedor, parent, false)
        return ViewHolder(v)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(p: Proveedor, listener: OnItemClickListener.ProveedorListener) =
            with(itemView) {
                textView1.text = p.razonSocial
                textView2.text = String.format("Total de Soles en el Mes : %s", p.totalMes)
                textView3.text = String.format("Asignado : %s", p.asignado)
                textView4.text = String.format("Terminados en el Dia : %s", p.terminado)
                textView5.text = String.format("Total de Soles en el Dia : %s", p.totaldia)
                imgMap.setOnClickListener { v -> listener.onItemClick(p, v, bindingAdapterPosition) }
                imgPeople.setOnClickListener { v -> listener.onItemClick(p, v, bindingAdapterPosition) }
            }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Proveedor>() {
            override fun areItemsTheSame(oldItem: Proveedor, newItem: Proveedor): Boolean =
                oldItem.id == newItem.id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Proveedor, newItem: Proveedor): Boolean =
                oldItem == newItem
        }
    }
}