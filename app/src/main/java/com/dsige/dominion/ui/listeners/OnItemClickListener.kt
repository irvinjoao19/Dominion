package com.dsige.dominion.ui.listeners

import android.view.View
import android.widget.EditText
import com.dsige.dominion.data.local.model.*
import com.google.android.material.checkbox.MaterialCheckBox

interface OnItemClickListener {
    interface MenuListener {
        fun onItemClick(m: MenuPrincipal, view: View, position: Int)
    }

    interface EstadoListener {
        fun onItemClick(e: Estado, view: View, position: Int)
    }

    interface DistritoListener {
        fun onItemClick(d: Distrito, view: View, position: Int)
    }

    interface GrupoListener {
        fun onItemClick(g: Grupo, view: View, position: Int)
    }

    interface OtListener {
        fun onItemClick(o: Ot, view: View, position: Int)
    }

    interface OtDetalleListener {
        fun onItemClick(o: OtDetalle, view: View, position: Int)
    }

    interface OtPhotoListener {
        fun onItemClick(o: OtPhoto, view: View, position: Int)
    }

    interface MaterialListener {
        fun onItemClick(m: Material, view: View, position: Int)
    }
}