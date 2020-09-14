package com.dsige.dominion.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.OtPlazoDetalle
import com.dsige.dominion.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_ot_plazo_detalle.view.*

class OtPlazoDetalleAdapter(private val listener: OnItemClickListener.OtPlazoDetalleListener) :
    PagedListAdapter<OtPlazoDetalle, OtPlazoDetalleAdapter.ViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = getItem(position)
        if (s != null) {
            holder.bind(s, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_ot_plazo_detalle, parent, false)
        return ViewHolder(v!!)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(p: OtPlazoDetalle, listener: OnItemClickListener.OtPlazoDetalleListener) =
            with(itemView) {
                textView1.text = p.tipoOt
                textView2.text = p.empresaContratista
                textView3.text = p.jefeCuadrilla
                textView4.text = p.nroObra
                textView5.text = p.direccion
                textView6.text = p.distrito
                textView7.text = p.fechaAsignacion
                textView8.text = p.fueraPlazoHoras
                textView9.text = p.fueraPlazoDias
                imgMap.setOnClickListener { v -> listener.onItemClick(p, v, adapterPosition) }
            }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<OtPlazoDetalle>() {
            override fun areItemsTheSame(
                oldItem: OtPlazoDetalle,
                newItem: OtPlazoDetalle
            ): Boolean =
                oldItem.otId == newItem.otId

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: OtPlazoDetalle,
                newItem: OtPlazoDetalle
            ): Boolean =
                oldItem == newItem
        }
    }
}