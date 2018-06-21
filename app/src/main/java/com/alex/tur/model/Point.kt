package com.alex.tur.model

import android.arch.persistence.room.ColumnInfo
import com.google.gson.annotations.SerializedName

data class Point(

        @ColumnInfo(name = "LAT")
        @SerializedName("lat")
        var lat: Double? = null,

        @ColumnInfo(name = "LNG")
        @SerializedName("lng")
        var lng: Double? = null
)