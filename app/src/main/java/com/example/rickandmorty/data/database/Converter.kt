package com.example.rickandmorty.data.database

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.rickandmorty.models.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@TypeConverters
class Converter {
    private val gson = Gson()

    @TypeConverter
    fun convertResultToJsonString(result: Result?): String {
//        val type = object : TypeToken<List<Locations>>() {}.type
//        val type = object : TypeToken<Result>() {}.type
        return gson.toJson(result, Result::class.java)
    }

    @TypeConverter
    fun convertJsonToResult(result: String): Result {
//        val type = object : TypeToken<Result>() {}.type
        return gson.fromJson(result, Result::class.java)
    }
}

