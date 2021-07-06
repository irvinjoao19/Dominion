package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class OperarioGps(
    var operarioId: Int,
    var latitud: String,
    var longitud: String,
    var fechaGPD: String,
    var fecha: String,
    var estado: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}