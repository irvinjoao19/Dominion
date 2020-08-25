package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Estado {
    @PrimaryKey(autoGenerate = true)
    var estadoId: Int = 0
    var abreviatura: String = ""
}