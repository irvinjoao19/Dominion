package com.dsige.dominion.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.OtDetalle
import com.dsige.dominion.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_ot_detalle.view.*

class OtDetalleAdapter(private val listener: OnItemClickListener.OtDetalleListener) :
    PagedListAdapter<OtDetalle, OtDetalleAdapter.ViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = getItem(position)
        if (s != null) {
            holder.setIsRecyclable(false)
            holder.bind(s, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_ot_detalle, parent, false)
        return ViewHolder(v!!)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(o: OtDetalle, listener: OnItemClickListener.OtDetalleListener) =
            with(itemView) {
                if (o.estado == 2) {
                    cardViewMateriales.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context, R.color.colorAccent
                        )
                    )
                }

                textView1.text = String.format("Cantidad que Recojio Desmonte")
                textView2.text =
                    String.format("%s * %s * %s = %s", o.largo, o.ancho, o.espesor, o.total)
                imgEdit.setOnClickListener { v -> listener.onItemClick(o, v, adapterPosition) }
            }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<OtDetalle>() {
            override fun areItemsTheSame(oldItem: OtDetalle, newItem: OtDetalle): Boolean =
                oldItem.otDetalleId == newItem.otDetalleId

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: OtDetalle, newItem: OtDetalle): Boolean =
                oldItem == newItem
        }
    }
}