package com.dsige.dominion.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.Ot
import com.dsige.dominion.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_ot.view.*

class OtAdapter(private val listener: OnItemClickListener.OtListener) :
    PagingDataAdapter<Ot, OtAdapter.ViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = getItem(position)
        if (s != null) {
            holder.bind(s, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_ot, parent, false)
        return ViewHolder(v!!)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bind(o: Ot, listener: OnItemClickListener.OtListener) =
            with(itemView) {
                textView1.text = o.nombreTipoOrden
                textView2.text = o.nroObra
                textView3.text = o.nombreEstado
                textView4.text = o.direccion
                textView5.text = o.nombreDistritoId
                textView6.text = String.format("Fec. Asignación: %s", o.fechaAsignacion)
                textView7.text = String.format("Dia Vcto: %s", o.vencimiento)
                textView8.text = String.format("Hora Asignación: %s", o.horaAsignacion)

                itemView.setOnClickListener { v -> listener.onItemClick(o, v, bindingAdapterPosition) }
            }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Ot>() {
            override fun areItemsTheSame(oldItem: Ot, newItem: Ot): Boolean =
                oldItem.otId == newItem.otId

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Ot, newItem: Ot): Boolean =
                oldItem == newItem
        }
    }
}