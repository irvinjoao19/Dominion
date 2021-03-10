package com.dsige.dominion.data.local.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
open class Usuario {
    @PrimaryKey(autoGenerate = true)
    var usuarioId: Int = 0
    var nroDoc: String = ""
    var apellidos: String = ""
    var nombres: String = ""
    var email: String = ""
    var tipoUsuarioId: Int = 0
    var perfilId: Int = 0
    var pass: String = ""
    var estado: Int = 0
    var personalId: Int = 0
    var empresaId: Int = 0
    var mensaje: String = ""
    var nombreEmpresa: String = ""

    var servicioId: Int = 0
    var nombreServicio: String = ""
    var tipo: Int = 0
    var nombreTipo: String = ""

    @Ignore
    var accesos: List<Accesos>? = ArrayList()
}