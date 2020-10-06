package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
open class OtDetalle {

    @PrimaryKey(autoGenerate = true)
    var otDetalleId: Int = 0
    var otId: Int = 0
    var tipoTrabajoId: Int = 0 // 6 medidas , 7 desmonte
    var tipoMaterialId: Int = 0
    var tipoDesmonteId: Int = 0
    var largo: Double = 0.0
    var ancho: Double = 0.0
    var espesor: Double = 0.0
    var total: Double = 0.0
    var nroPlaca: String = ""
    var m3Vehiculo: Double = 0.0
    var estado: Int = 0 // 2 -> por completar 1 -> completado // 3 -> enviado

    var nombreTipoMaterial: String = ""
    var nombreTipoDemonte:String = ""

    @Ignore
    var photos: List<OtPhoto> = ArrayList()

}