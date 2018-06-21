package com.alex.tur.model

import android.arch.persistence.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OrderDescription(

        @ColumnInfo(name = "PICTURE")
        @SerializedName("picture")
        var picture: String? = null,

        @ColumnInfo(name = "DESCRIPTION")
        @SerializedName("briefly_description")
        var brieflyDescription: String? = null
): Serializable