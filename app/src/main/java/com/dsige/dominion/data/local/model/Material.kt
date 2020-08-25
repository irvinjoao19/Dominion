package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Material {
    @PrimaryKey(autoGenerate = true)
    var detalleId: Int = 0
    var grupoId: Int = 0
    var codigo: String = ""
    var descripcion: String = ""
    var estado: Int = 0
}