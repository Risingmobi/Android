package com.alex.tur.ext

import android.location.Location
import com.alex.tur.model.Driver
import com.google.android.gms.maps.model.LatLng

fun Driver.fetchServicesNames(): String {
    availableServices?.let {
        var s = ""
        for (service in it) {
            s += "${service.naming}, "
        }
        return if (s.isNotEmpty()) {
            s.substring(0, s.lastIndex - 1)
        } else {
            s
        }
    }?:run {
        return ""
    }
}

fun Driver.fetchLatLng(): LatLng? {
    if(lat != null && lng != null) {
        return LatLng(lat!!, lng!!)
    }
    return null
}