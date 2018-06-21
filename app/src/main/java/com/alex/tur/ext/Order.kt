package com.alex.tur.ext

import com.alex.tur.model.Order
import com.google.android.gms.maps.model.LatLng

fun Order.fetchLatLng(): LatLng? {
    if(lat != null && lng != null) {
        return LatLng(lat!!, lng!!)
    }
    return null
}