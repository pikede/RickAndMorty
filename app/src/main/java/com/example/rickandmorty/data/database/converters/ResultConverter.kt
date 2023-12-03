package com.example.rickandmorty.data.database.converters

import androidx.room.TypeConverter
import com.example.rickandmorty.models.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ResultConverter {
    private val gson = Gson()

    @TypeConverter
    fun convertResultToJsonString(result: Result?): String? {
        return gson.toJson(result)
    }

    @TypeConverter
    fun convertJsonToResult(result: String): Result? {
        return gson.fromJson(result, Result::class.java)
    }
}