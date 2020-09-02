package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Proveedor {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var empresaId: Int = 0
    var razonSocial: String = ""
    var totalMes: Double = 0.0
    var asignado: Double = 0.0
    var terminado: Double = 0.0
    var totaldia: Double = 0.0
}