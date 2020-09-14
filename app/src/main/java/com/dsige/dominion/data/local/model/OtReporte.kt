package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class OtReporte {

    @PrimaryKey(autoGenerate = true)
    var otId: Int = 0
    var nombreTipoOrdenTrabajo: String = ""
    var nombreArea: String = ""
    var nroObra: String = ""
    var direccion: String = ""
    var nombreDistrito: String = ""
    var fechaAsignacion: String = ""
    var nombreEmpresa: String = ""
    var personalJefeCuadrillaId: Int = 0
    var nombreJC: String = ""
    var estado: String = ""
    var nombreEstado: String = ""
    var vencimiento: String = ""
    var latitud: String = ""
    var longitud: String = ""
}