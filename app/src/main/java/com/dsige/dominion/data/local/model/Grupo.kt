package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Grupo {
    @PrimaryKey(autoGenerate = true)
    var grupoId: Int = 0
    var descripcion: String = ""
}