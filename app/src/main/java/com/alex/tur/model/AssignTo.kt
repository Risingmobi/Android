package com.alex.tur.model

import android.arch.persistence.room.ColumnInfo
import com.google.gson.annotations.SerializedName

data class AssignTo(

        @ColumnInfo(name = "NAME")
        @SerializedName("name")
        var name: String? = null,

        @ColumnInfo(name = "AVATAR")
        @SerializedName("avatar")
        var avatar: String? = null
)