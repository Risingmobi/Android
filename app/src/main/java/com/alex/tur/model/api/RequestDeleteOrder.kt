package com.alex.tur.model.api

import com.google.gson.annotations.SerializedName

data class RequestDeleteOrder(

        @SerializedName("reason")
        val reason: String
)