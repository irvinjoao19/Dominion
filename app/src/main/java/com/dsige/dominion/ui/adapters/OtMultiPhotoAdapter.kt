package com.dsige.dominion.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsige.dominion.R
import com.dsige.dominion.helper.Util
import com.dsige.dominion.ui.listeners.OnItemClickListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_ot_multi_photo.view.*
import java.io.File

class OtMultiPhotoAdapter(private val listener: OnItemClickListener.OtMultiPhotoListener) :
    RecyclerView.Adapter<OtMultiPhotoAdapter.ViewHolder>() {

    private var photos = emptyList<String>()

    fun addItems(list: List<String>) {
        photos = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_ot_multi_photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photos[position], listener)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(o: String, listener: OnItemClickListener.OtMultiPhotoListener) = with(itemView) {
            val f = File(Util.getFolder(itemView.context), o)
            Picasso.get().load(f).into(imageViewPhoto)
            itemView.setOnClickListener { v -> listener.onItemClick(o, v, adapterPosition) }
        }
    }
}