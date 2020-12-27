package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Sed {
    @PrimaryKey
    var codigo: String = ""
    var distrito: String = ""
    var distritoId: Int = 0
}