package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class OperarioBattery(
    var operarioId: Int,
    var gpsActivo: Int,
    var estadoBateria: Int,
    var fecha: String,
    var modoAvion: Int,
    var planDatos: Int,
    var estado: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}