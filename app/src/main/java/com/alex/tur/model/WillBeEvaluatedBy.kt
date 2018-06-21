package com.alex.tur.model

import android.arch.persistence.room.ColumnInfo
import com.google.gson.annotations.SerializedName

data class WillBeEvaluatedBy(

        @ColumnInfo(name = "NAME")
        @SerializedName("name")
        var name: String? = null,

        @ColumnInfo(name = "AVATAR")
        @SerializedName("avatar")
        var avatar: String? = null,

        @ColumnInfo(name = "VEHICLE_MODEL")
        @SerializedName("vehicle_model")
        var vehicleModel: String? = null,

        @ColumnInfo(name = "VEHICLE_NUMBER")
        @SerializedName("vehicle_number")
        var vehicleNumber: String? = null
)