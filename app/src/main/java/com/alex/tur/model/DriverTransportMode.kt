package com.alex.tur.model

import com.google.gson.annotations.SerializedName

enum class DriverTransportMode {
    @SerializedName("driving")
    DRIVING,
    @SerializedName("walking")
    WALKING,
    @SerializedName("bicycling")
    BICYCLING
}