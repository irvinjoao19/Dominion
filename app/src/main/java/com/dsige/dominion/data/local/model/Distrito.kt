package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Distrito {
    @PrimaryKey(autoGenerate = true)
    var distritoId: Int = 43
    var nombreDistrito: String = ""
    var estado: Int = 0
}