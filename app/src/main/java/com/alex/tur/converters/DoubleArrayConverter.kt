package com.alex.tur.converters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DoubleArrayConverter {

    @TypeConverter
    fun fromDb(value: String?): DoubleArray {
        value?.let {
            return Gson().fromJson(value, object: TypeToken<DoubleArray>(){}.type)
        }
        return DoubleArray(0)
    }

    @TypeConverter
    fun toDb(array: DoubleArray?): String {
        array?.let {
            return Gson().toJson(it)
        }
        return Gson().toJson(DoubleArray(0))
    }
}