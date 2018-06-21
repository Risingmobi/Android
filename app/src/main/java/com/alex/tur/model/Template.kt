package com.alex.tur.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Template(

        @SerializedName("id")
        var idRemote: Long? = null,

        @SerializedName("lat")
        var lat: Double? = null,

        @SerializedName("lng")
        var lng: Double? = null,

        @SerializedName("time")
        var time: String? = null,

        @SerializedName("day_of_week")
        var dayOfWeek: DayOfWeek? = null,

        @SerializedName("request_descr")
        var requestDescription: OrderDescription? = null,

        @SerializedName("service_descr")
        var serviceDescription: ServiceDescription? = null
): Serializable {

        data class ServiceDescription(
                @SerializedName("id")
                var id: Long? = null
        ): Serializable
}