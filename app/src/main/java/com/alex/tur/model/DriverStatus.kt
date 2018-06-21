package com.alex.tur.model

import com.google.gson.annotations.SerializedName

enum class DriverStatus {
    @SerializedName("Active")
    ACTIVE,
    @SerializedName("Pending")
    PENDING,
    @SerializedName("Inactive")
    INACTIVE
}