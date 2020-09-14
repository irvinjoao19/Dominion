package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class OtPlazoDetalle {
    @PrimaryKey
    var otId: Int = 0
    var descripcionEstado: String = ""
    var tipoOt: String = ""
    var nroObra: String = ""
    var direccion: String = ""
    var distrito: String = ""
    var latitud: String = ""
    var longitud: String = ""
    var fechaAsignacion: String = ""
    var fechaMovil: String = ""
    var empresaContratista: String = ""
    var jefeCuadrilla: String = ""
    var fueraPlazoHoras: String = ""
    var fueraPlazoDias: String = ""
    var tipoTrabajoId: Int = 0
    var distritoId: Int = 0
    var referencia: String = ""
    var descripcionOt: String = ""
    var estadoId: Int = 0
}