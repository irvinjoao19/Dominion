package com.dsige.dominion.helper

open class MessageError {

    var Message: String? = ""
    var ExceptionMessage: String? = ""
    var ExceptionType: String? = ""
    var StackTrace: String? = ""

    constructor()

    constructor(Message: String?, ExceptionMessage: String?, ExceptionType: String?, StackTrace: String?) {
        this.Message = Message
        this.ExceptionMessage = ExceptionMessage
        this.ExceptionType = ExceptionType
        this.StackTrace = StackTrace
    }
}