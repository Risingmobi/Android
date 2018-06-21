package com.alex.tur.ext

import com.alex.tur.model.DriverPath
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

fun DriverPath.buildStartLatLng(): LatLng? {
    if (startPoint?.lat != null && startPoint?.lng != null) {
        return LatLng(startPoint!!.lat!!, startPoint!!.lng!!)
    }
    return null
}

fun DriverPath.buildEndLatLng(): LatLng? {
    if (endPoint?.lat != null && endPoint?.lng != null) {
        return LatLng(endPoint!!.lat!!, endPoint!!.lng!!)
    }
    return null
}

fun DriverPath.buildLatLngBounds(): LatLngBounds? {
    if (route != null && route!!.isNotEmpty()) {
        val builder = LatLngBounds.Builder()
        for (i in route!!.indices) {
            val line = route!![i]
            if (i == 0) {
                if (line.start != null && line.start?.lat != null && line.start?.lng != null) {
                    builder.include(LatLng(line.start!!.lat!!, line.start!!.lng!!))
                }
                if (line.end != null && line.end?.lat != null && line.end?.lng != null) {
                    builder.include(LatLng(line.end!!.lat!!, line.end!!.lng!!))
                }
            } else {
                if (line.end != null && line.end?.lat != null && line.end?.lng != null) {
                    builder.include(LatLng(line.end!!.lat!!, line.end!!.lng!!))
                }
            }
        }
        return builder.build()
    } else {
        return null
    }
}