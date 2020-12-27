package com.dsige.dominion.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.OtPhoto
import com.dsige.dominion.helper.Util
import com.dsige.dominion.ui.listeners.OnItemClickListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_ot_photo_baja_tension.view.*
import java.io.File

class OtPhotoBajaTensionAdapter(private val listener: OnItemClickListener.OtPhotoListener) :
    RecyclerView.Adapter<OtPhotoBajaTensionAdapter.ViewHolder>() {

    private var photos = emptyList<OtPhoto>()

    fun addItems(list: List<OtPhoto>) {
        photos = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_ot_photo_baja_tension, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photos[position], listener)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(o: OtPhoto, listener: OnItemClickListener.OtPhotoListener) = with(itemView) {
            val f = File(Util.getFolder(itemView.context), o.urlPhoto)
            Picasso.get().load(f).into(imgPhoto)
            imgDelete.setOnClickListener { v -> listener.onItemClick(o, v, adapterPosition) }
            itemView.setOnClickListener { v -> listener.onItemClick(o, v, adapterPosition) }
        }
    }
}