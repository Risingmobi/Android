package com.alex.tur.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import com.google.gson.annotations.SerializedName
import java.util.*

data class DriverPath(

        @Embedded(prefix = "START_POINT_")
        @SerializedName("start_point")
        var startPoint: Point? = null,

        @Embedded(prefix = "END_POINT_")
        @SerializedName("end_point")
        var endPoint: Point? = null,

        @ColumnInfo(name = "ROUTE")
        @SerializedName("route")
        var route: Array<Line>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DriverPath) return false

        if (startPoint != other.startPoint) return false
        if (endPoint != other.endPoint) return false
        if (!Arrays.equals(route, other.route)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = startPoint?.hashCode() ?: 0
        result = 31 * result + (endPoint?.hashCode() ?: 0)
        result = 31 * result + Arrays.hashCode(route)
        return result
    }
}