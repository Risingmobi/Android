package com.alex.tur.ext

import android.location.Location
import com.google.android.gms.maps.model.LatLng

fun Location.fetchLatLng(): LatLng {
    return LatLng(latitude, longitude)
}