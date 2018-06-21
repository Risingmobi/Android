package com.alex.tur.converters

import android.arch.persistence.room.TypeConverter
import com.alex.tur.model.PaymentStatus

class PaymentStatusConverter {

    @TypeConverter
    fun fromDb(value: String?): PaymentStatus? {
        return value?.let { PaymentStatus.valueOf(value) }
    }

    @TypeConverter
    fun toDb(value: PaymentStatus?): String? {
        return value?.name
    }
}