package com.alex.tur.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "CUSTOMER")
data class Customer(

        @PrimaryKey
        @ColumnInfo(name = "ID")
        var idLocal: Long? = null,

        @ColumnInfo(name = "ID_REMOTE")
        @SerializedName("id")
        var idRemote: Long? = null,

        @SerializedName("name")
        @ColumnInfo(name = "USER_NAME")
        var name: String? = null,

        @SerializedName("email")
        @ColumnInfo(name = "EMAIL")
        var email: String? = null,

        @SerializedName("password")
        @ColumnInfo(name = "PASSWORD")
        var password: String? = null,

        @SerializedName("avatar")
        @ColumnInfo(name = "AVATAR_URL")
        var avatar: String? = null,

        @ColumnInfo(name = "LAT")
        @SerializedName("lat")
        var lat: Double? = null,

        @ColumnInfo(name = "LNG")
        @SerializedName("lng")
        var lng: Double? = null,

        @ColumnInfo(name = "PHONE")
        @SerializedName("phone_number")
        var phone: String? = null,

        @ColumnInfo(name = "ADDRESS_STRING")
        @SerializedName("address_string")
        var addressString: String? = null
)
