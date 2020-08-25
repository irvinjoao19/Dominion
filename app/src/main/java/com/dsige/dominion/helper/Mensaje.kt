package com.dsige.dominion.helper

open class Mensaje {

    var codigoBase: Int = 0
    var codigoRetorno: Int = 0
    var codigoBaseCliente : Int = 0
    var codigoRetornoCliente : Int = 0
    var mensaje: String = ""
    var detalle: List<MensajeDetalle>? = null
}