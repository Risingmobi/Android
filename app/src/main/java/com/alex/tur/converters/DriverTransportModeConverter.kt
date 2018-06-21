package com.alex.tur.converters

import android.arch.persistence.room.TypeConverter
import com.alex.tur.model.DriverTransportMode
import timber.log.Timber

class DriverTransportModeConverter {

    @TypeConverter
    fun fromDb(value: String?): DriverTransportMode? {
        return value?.let { DriverTransportMode.valueOf(value) }
    }

    @TypeConverter
    fun toDb(value: DriverTransportMode?): String? {
        return value?.name
    }
}