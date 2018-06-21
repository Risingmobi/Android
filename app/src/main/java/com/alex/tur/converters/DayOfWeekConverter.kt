package com.alex.tur.converters

import android.arch.persistence.room.TypeConverter
import com.alex.tur.model.DayOfWeek

class DayOfWeekConverter {

    @TypeConverter
    fun fromDb(value: String?): DayOfWeek? {
        return value?.let { DayOfWeek.valueOf(value) }
    }

    @TypeConverter
    fun toDb(value: DayOfWeek?): String? {
        return value?.name
    }
}