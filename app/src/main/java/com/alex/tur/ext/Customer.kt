package com.alex.tur.ext

import com.alex.tur.model.Customer
import com.google.android.gms.maps.model.LatLng

fun Customer.fetchLatLng(): LatLng? {
    if(lat != null && lng != null) {
        return LatLng(lat!!, lng!!)
    }
    return null
}