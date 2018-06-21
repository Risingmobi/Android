package com.alex.tur.model

import com.google.gson.annotations.SerializedName

data class RequestService(

        @SerializedName("id")
        var id: Long? = null,

        @SerializedName("service_descr")
        var service: Service? = null,

        @SerializedName("request_descr")
        var requestDescription: OrderDescription? = null,

        @SerializedName("address")
        var address: String? = null,

        @SerializedName("lat")
        var lat: Double? = null,

        @SerializedName("lng")
        var lng: Double? = null,

        @SerializedName("status")
        var status: OrderStatus? = null,

        @SerializedName("payment_status")
        var paymentStatus: PaymentStatus? = null
)