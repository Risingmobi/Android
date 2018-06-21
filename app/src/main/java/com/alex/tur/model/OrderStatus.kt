package com.alex.tur.model

import com.google.gson.annotations.SerializedName

enum class OrderStatus {
    @SerializedName("Active")
    ACTIVE,
    @SerializedName("Pending")
    PENDING,
    @SerializedName("Close")
    CLOSE
}