package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class OtPhoto {

    @PrimaryKey(autoGenerate = true)
    var otPhotoId: Int = 0
    var otDetalleId: Int = 0
    var nombrePhoto: String = ""
    var urlPhoto: String = ""
    var estado: Int = 0
    var otId: Int = 0
}