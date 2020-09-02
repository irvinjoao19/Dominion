package com.dsige.dominion.data.local.model

open class MapLegs {
    var duration: MapDuration? = null
    var steps: List<MapSteps> = ArrayList()
    var start_address: String = ""
    var start_location: MapStartLocation? = null
}