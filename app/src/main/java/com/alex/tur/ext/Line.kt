package com.alex.tur.ext

import com.alex.tur.model.Line
import com.google.android.gms.maps.model.LatLng

fun Line.buildLine(): Array<LatLng>? {
    if (start?.lat != null && start?.lng != null && end?.lat != null && end?.lng != null) {
        return arrayOf(LatLng(start!!.lat!!, start!!.lng!!), LatLng(end!!.lat!!, end!!.lng!!))
    }
    return null
}