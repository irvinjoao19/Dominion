package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Grupo {
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
    var grupoId: Int = 0
    var descripcion: String = ""
    var servicioId : Int = 0
}