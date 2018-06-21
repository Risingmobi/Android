package com.alex.tur.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import com.google.gson.annotations.SerializedName
import java.util.*

data class DurationAndDistance(

        @Embedded(prefix = "DURATION_")
        @SerializedName("duration")
        var duration: Duration? = null,

        @Embedded(prefix = "DISTANCE_")
        @SerializedName("distance")
        var distance: Distance? = null,

        @ColumnInfo(name = "POINT")
        @SerializedName("point")
        var point: DoubleArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DurationAndDistance) return false

        if (duration != other.duration) return false
        if (distance != other.distance) return false
        if (!Arrays.equals(point, other.point)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = duration?.hashCode() ?: 0
        result = 31 * result + (distance?.hashCode() ?: 0)
        result = 31 * result + (point?.let { Arrays.hashCode(it) } ?: 0)
        return result
    }
}