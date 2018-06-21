package com.alex.tur.model

import com.google.gson.annotations.SerializedName

data class Line(
        @SerializedName("start")
        var start: Point? = null,

        @SerializedName("end")
        var end: Point? = null
)