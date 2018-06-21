package com.alex.tur.converters

import android.arch.persistence.room.TypeConverter
import com.alex.tur.model.Service
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DriverServiceListConverter {

    @TypeConverter
    fun fromDb(value: String?): MutableList<Service> {
        value?.let {
            return Gson().fromJson(value, object: TypeToken<MutableList<Service>>(){}.type)
        }
        return mutableListOf()
    }

    @TypeConverter
    fun toDb(list: MutableList<Service>?): String {
        list?.let {
            return Gson().toJson(it)
        }
        return Gson().toJson(emptyList<Service>())
    }
}