package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class JefeCuadrilla {

    @PrimaryKey(autoGenerate = true)
    var cuadrillaId: Int = 0
    var empresaId: Int = 0
    var empresa: String = ""
    var jefeCuadrillaId: Int = 0
    var nombreJefe: String = ""
    var asignado: Double = 0.0
    var terminado: Double = 0.0
    var pendiente: Double = 0.0
    var latitud: String = ""
    var longitud: String = ""
    var icono: String = ""
}