package com.dsige.dominion.data.local.model

import androidx.room.DatabaseView

@DatabaseView("SELECT empresaId ,servicioId ,tipoOrdenId ,COUNT(tipoOrdenId) as cantidad,usuarioId " +
        "FROM Ot WHERE activeNotificacion = 1 " +
        "GROUP BY empresaId,servicioId,tipoOrdenId,usuarioId ORDER BY COUNT(tipoOrdenId) ")
open class OtNotify {
    var empresaId: Int = 0
    var servicioId: Int = 0
    var tipoOrdenId: Int = 0
    var cantidad: Int = 0
    var usuarioId: Int = 0
}