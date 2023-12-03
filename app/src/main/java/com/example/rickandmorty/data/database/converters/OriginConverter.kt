package com.example.rickandmorty.data.database.converters

import androidx.room.TypeConverter
import com.example.rickandmorty.models.Origin
import com.google.gson.Gson

class OriginConverter {
    private val gson = Gson()

    @TypeConverter
    fun convertFromOriginToJson(origin: Origin?): String? {
        return gson.toJson(origin)
    }

    @TypeConverter
    fun convertFromJsonToOrigin(origin: String?): Origin? {
        return gson.fromJson(origin, Origin::class.java)
    }
}