package com.dsige.dominion.ui.listeners

import android.view.View
import com.dsige.dominion.data.local.model.*

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

    interface ServicioListener {
        fun onItemClick(s: Servicio, view: View, position: Int)
    }

    interface ProveedorListener {
        fun onItemClick(p: Proveedor, view: View, position: Int)
    }

    interface OtPlazoListener {
        fun onItemClick(o: OtPlazo, view: View, position: Int)
    }

    interface OtPlazoDetalleListener {
        fun onItemClick(o: OtPlazoDetalle, view: View, position: Int)
    }
}