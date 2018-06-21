package com.alex.tur.converters

import android.arch.persistence.room.TypeConverter
import com.alex.tur.model.DriverStatus
import timber.log.Timber

class DriverStatusConverter {

    @TypeConverter
    fun fromDb(value: String?): DriverStatus? {
        Timber.d("fromDb %s", value)
        return value?.let { DriverStatus.valueOf(value) }
    }

    @TypeConverter
    fun toDb(value: DriverStatus?): String? {
        Timber.d("toDb %s", value?.name)
        return value?.name
    }
}