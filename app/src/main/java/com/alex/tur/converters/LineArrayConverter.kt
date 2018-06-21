package com.alex.tur.converters

import android.arch.persistence.room.TypeConverter
import com.alex.tur.model.Line
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LineArrayConverter {

    @TypeConverter
    fun fromDb(value: String?): Array<Line> {
        value?.let {
            return Gson().fromJson(value, object: TypeToken<Array<Line>>(){}.type)
        }
        return emptyArray()
    }

    @TypeConverter
    fun toDb(array: Array<Line>?): String {
        array?.let {
            return Gson().toJson(array)
        }
        return Gson().toJson(emptyArray<Line>())
    }
}