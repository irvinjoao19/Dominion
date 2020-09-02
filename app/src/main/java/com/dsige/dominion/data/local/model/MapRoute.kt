package com.dsige.dominion.data.local.model

import com.google.gson.annotations.SerializedName

open class MapRoute {
    @SerializedName("overview_polyline")
    var mapOverviewPolyLine: MapOverviewPolyline? = null
    var legs: List<MapLegs> = ArrayList()
}