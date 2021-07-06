package com.dsige.dominion.helper

import com.dsige.dominion.data.local.model.MapPrincipal

interface TaskLoadedCallback {
    fun onTaskDone(vararg values: Any)

    fun onTaskRoutes(values: MapPrincipal)
}
