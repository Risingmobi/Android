package com.alex.tur.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Ignore
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Company(

        @SerializedName("id")
        @ColumnInfo(name = "ID")
        var id: String? = null,

        @SerializedName("service_id")
        @ColumnInfo(name = "SERVICE_ID")
        var service_id: Long? = null,

        @SerializedName("naming")
        @ColumnInfo(name = "NAMING")
        var naming: String? = null,

        @SerializedName("picture")
        @ColumnInfo(name = "PICTURE")
        var picture: String? = null,

        @SerializedName("cost")
        @ColumnInfo(name = "COST")
        var cost: Float? = null

): Serializable