package com.alex.tur.model

import android.arch.persistence.room.*
import com.google.gson.annotations.SerializedName

@Entity(tableName = "ORDERS")
data class Order(

        @PrimaryKey
        @SerializedName("id")
        var id: Int? = null,

        @Embedded(prefix = "ASSIGN_TO_")
        @SerializedName("assign_to")
        var assignTo: AssignTo? = null,

        @ColumnInfo(name = "LAT")
        @SerializedName("lat")
        var lat: Double? = null,

        @ColumnInfo(name = "LNG")
        @SerializedName("lng")
        var lng: Double? = null,

        @Embedded(prefix = "EVAL_BY_")
        @SerializedName("will_be_evaluated_by")
        var willBeEvaluatedBy: WillBeEvaluatedBy? = null,

        @Embedded(prefix = "SERVICE_")
        @SerializedName("service_descr")
        var service: Service? = null,

        @Embedded(prefix = "ORDER_DESC_")
        @SerializedName("request_descr")
        var orderDescription: OrderDescription? = null,

        @Embedded(prefix = "DUR_AND_DIST_")
        @SerializedName("estimation_duration_and_distance")
        var estimationDurationAndDistance: DurationAndDistance? = null,

        @Embedded(prefix = "DRIVER_PATH_")
        @SerializedName("driver_path")
        var driverPath: DriverPath? = null,

        @ColumnInfo(name = "ADDRESS_STRING")
        @SerializedName("address")
        var address: String? = null,

        @ColumnInfo(name = "ORDER_STATUS")
        @SerializedName("status")
        var status: OrderStatus? = null,

        @ColumnInfo(name = "PAYMENT_STATUS")
        @SerializedName("payment_status")
        var paymentStatus: PaymentStatus? = null
) {
        @Ignore
        var isReadyForCompletion: Boolean = false
}