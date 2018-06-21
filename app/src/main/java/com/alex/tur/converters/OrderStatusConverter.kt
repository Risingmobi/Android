package com.alex.tur.converters

import android.arch.persistence.room.TypeConverter
import com.alex.tur.model.OrderStatus
import timber.log.Timber

class OrderStatusConverter {

    @TypeConverter
    fun fromDb(value: String?): OrderStatus? {
        Timber.d("fromDb %s", value)
        return value?.let { OrderStatus.valueOf(value) }
    }

    @TypeConverter
    fun toDb(value: OrderStatus?): String? {
        Timber.d("toDb %s", value?.name)
        return value?.name
    }
}