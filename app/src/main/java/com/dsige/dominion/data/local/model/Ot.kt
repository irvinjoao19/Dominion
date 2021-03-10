package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
open class Ot {
    @PrimaryKey(autoGenerate = true)
    var otId: Int = 0
    var tipoOrdenId: Int = 0
    var nombreTipoOrden: String = ""
    var servicioId: Int = 0
    var nombreArea: String = ""
    var nroObra: String = ""
    var direccion: String = ""
    var distritoId: Int = 0
    var nombreDistritoId: String = ""
    var referenciaOt: String = ""
    var descripcionOt: String = ""
    var fechaRegistro: String = ""
    var fechaAsignacion: String = ""
    var horaAsignacion: String = ""
    var empresaId: Int = 0
    var nombreEmpresa: String = ""
    var tipoEmpresa: String = ""
    var personalJCId: Int = 0
    var nombreJO: String = ""
    var otOrigenId: Int = 0

    var nombreEstado: String = ""
    var vencimiento: String = ""

    var observacion: String = ""
    var motivoPrioridadId: Int = 0
    var nombrePrioridad: String = ""
    var observaciones: String = ""
    var ordenamientoOt: Int = 0
    var latitud: String = ""
    var longitud: String = ""
    var usuarioId: Int = 0
    var identity: Int = 0
    var distritoIdGps: Int = 0
    var suministroTD: String = ""
    var nroSed: String = ""
    var fotoAnterior: String = ""
    var fotoCabecera: String = ""
    var viajeIndebido: Int = 0

    var estadoId: Int = 0 // 4 -> ejecutado  // 5 enviado aprobar
    var estado: Int = 0 // 1 -> para enviar , 2 -> incompleto  , 0 -> desabilitado
    var fechaXOt: String = "" // solo un nro de ot por dia

    var activeNotificacion: Int = 0

    var fechaInicioTrabajo: String = ""
    var fechaFinTrabajo: String = ""
    var conDesmonte : Boolean = true // 1 -> permite que se pueda guardar la ot sin necesidad de un desmonte


    @Ignore
    var detalles: List<OtDetalle> = ArrayList()
}