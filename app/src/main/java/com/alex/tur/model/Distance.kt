package com.alex.tur.model

import android.arch.persistence.room.ColumnInfo
import com.google.gson.annotations.SerializedName

data class Distance(

        @ColumnInfo(name = "TEXT")
        @SerializedName("text")
        var text: String? = null,

        @ColumnInfo(name = "VALUE")
        @SerializedName("value")
        var value: Int? = null
)