package com.alex.tur.model

import android.arch.persistence.room.*
import com.google.gson.annotations.SerializedName

@Entity(tableName = "DRIVER")
data class Driver(
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

        @ColumnInfo(name = "VEHICLE_NUMBER")
        @SerializedName("vehicle_number")
        var vehicleNumber: String? = null,

        @ColumnInfo(name = "VEHICLE_MODEL")
        @SerializedName("vehicle_model")
        var vehicleModel: String? = null,

        @ColumnInfo(name = "STATUS")
        @SerializedName("status")
        var status: DriverStatus? = null,

        @ColumnInfo(name = "TRANSPORT_MODE")
        @SerializedName("transport_mode")
        var transportMode: DriverTransportMode? = null,

        @SerializedName("available_services")
        var availableServices: MutableList<Service>? = null,

        @SerializedName("company")
        @Embedded(prefix = "DRIVER_COMPANY_")
        var company: Company? = null
)