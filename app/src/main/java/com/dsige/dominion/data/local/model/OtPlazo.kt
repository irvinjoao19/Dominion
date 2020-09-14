package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class OtPlazo {
    @PrimaryKey
    var empresaId: Int = 0
    var razonSocial: String = ""
    var cantidad: Int = 0
}