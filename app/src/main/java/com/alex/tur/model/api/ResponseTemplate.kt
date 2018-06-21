package com.alex.tur.model.api

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.alex.tur.model.DayOfWeek
import com.alex.tur.model.Order
import com.google.gson.annotations.SerializedName

@Entity(tableName = "TEMPLATE")
data class ResponseTemplate(
        @PrimaryKey
        @ColumnInfo(name = "ID")
        var idLocal: Long? = null,

        @SerializedName("id")
        @ColumnInfo(name = "ID_REMOTE")
        var idRemote: Long? = null,

        @SerializedName("name")
        @ColumnInfo(name = "NAME")
        var name: String? = null,

        @SerializedName("crontab")
        @Embedded(prefix = "SCHEDULE_")
        var schedule: Schedule? = null,

        @SerializedName("service")
        @Embedded(prefix = "ORDER_")
        var order: Order? = null
) {

    data class Schedule(
            @PrimaryKey
            @SerializedName("id")
            @ColumnInfo(name = "ID")
            var id: Long? = null,

            @SerializedName("day_of_week")
            @ColumnInfo(name = "DAY_OF_WEEK")
            var dayOfWeek: DayOfWeek? = null,

            @SerializedName("hour")
            @ColumnInfo(name = "HOUR")
            var hour: String? = null,

            @SerializedName("minute")
            @ColumnInfo(name = "MINUTE")
            var minute: String? = null
    )
}