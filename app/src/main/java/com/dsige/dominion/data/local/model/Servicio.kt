package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Servicio {

    @PrimaryKey
    var servicioId: Int = 0
    var usuarioId: Int = 0
    var nombreServicio: String = ""
}