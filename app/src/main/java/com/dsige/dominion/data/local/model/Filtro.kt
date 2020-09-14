package com.dsige.dominion.data.local.model

open class Filtro {

    var empresaId: Int = 0
    var personalId: Int = 0
    var usuarioId: Int = 0
    var pageIndex: Int = 0
    var pageSize: Int = 0
    var search: String = ""
    var login: String = ""
    var pass: String = ""
    var imei: String = "" // tipo Repor
    var version: String = "" // fecha
    var departamentoId: String = ""
    var provinciaId: String = ""
    var distritoId: String = ""
    var fecha: String = ""

    var localId: Int = 0
    var distritoRId: Int = 0

    var estadoId: Int = 0
    var tipo: Int = 0
    var servicioId: Int = 0

    constructor()

    constructor(login: String) {
        this.login = login
    }

    constructor(login: String, pass: String, imei: String, version: String) {
        this.login = login
        this.pass = pass
        this.imei = imei
        this.version = version
    }

    constructor(usuarioId: Int, search: String, pageIndex: Int, pageSize: Int) {
        this.usuarioId = usuarioId
        this.search = search
        this.pageIndex = pageIndex
        this.pageSize = pageSize
    }

    constructor(usuarioId: Int, empresaId: Int, personalId: Int) {
        this.usuarioId = usuarioId
        this.empresaId = empresaId
        this.personalId = personalId
    }

    constructor(search: String, estadoId: Int, tipo: Int) {
        this.search = search
        this.estadoId = estadoId
        this.tipo = tipo
    }

    constructor(fecha: String, imei: String, servicioId: Int, tipo: Int) {
        this.fecha = fecha
        this.imei = imei
        this.servicioId = servicioId
        this.tipo = tipo
    }

    constructor(fecha: String, servicioId: Int, tipo: Int, empresaId: Int) {
        this.fecha = fecha
        this.servicioId = servicioId
        this.tipo = tipo
        this.empresaId = empresaId
    }

    constructor(empresaId: Int, personalId: Int) {
        this.empresaId = empresaId
        this.personalId = personalId
    }

    constructor(servicioId: Int, tipo: Int, empresaId: Int, usuarioId: Int) {
        this.servicioId = servicioId
        this.tipo = tipo
        this.empresaId = empresaId
        this.usuarioId = usuarioId
    }



}