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
    var imei: String = ""
    var version: String = ""
    var departamentoId: String = ""
    var provinciaId: String = ""
    var distritoId: String = ""

    var localId : Int = 0
    var distritoRId : Int = 0

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

    constructor(empresaId: Int, personalId: Int) {
        this.empresaId = empresaId
        this.personalId = personalId
    }
}