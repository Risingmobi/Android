package com.alex.tur.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Ignore
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Service(

        @SerializedName("id")
        @ColumnInfo(name = "ID")
        var id: Long? = null,

        @SerializedName("naming")
        @ColumnInfo(name = "NAMING")
        var naming: String? = null,

        @SerializedName("description")
        @ColumnInfo(name = "DESCRIPTION")
        var description: String? = null,

        @SerializedName("cost")
        @ColumnInfo(name = "COST")
        var cost: String? = null,

        @SerializedName("picture")
        @ColumnInfo(name = "PICTURE")
        var picture: String? = null,

        @SerializedName("company")
        @Embedded(prefix = "COMPANY_")
        var company: Company? = null,

        @Ignore
        var isChecked: Boolean = false,

        @Ignore
        var isMine: Boolean = false

): Serializable