package com.example.rickandmorty.data.database.converters

import androidx.room.TypeConverter
import com.example.rickandmorty.models.Location
import com.google.gson.Gson

class LocationConverter {
    private val gson = Gson()

    @TypeConverter
    fun convertLocationToJsonString(location: Location?): String? {
        return gson.toJson(location)
    }

    @TypeConverter
    fun convertJsonToLocation(location: String): Location? {
        return gson.fromJson(location, Location::class.java)
    }
}

