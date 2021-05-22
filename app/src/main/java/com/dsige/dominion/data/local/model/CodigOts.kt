package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class CodigOts {
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
    var codigo : String = ""
}