package com.example.rickandmorty.data.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EpisodeConverter {
    private val gson = Gson()
    private val episodeToken = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun convertEpisodeToJson(episode: List<String>?): String? {
        return gson.toJson(episode)
    }

    @TypeConverter
    fun convertJsonToEpisode(episode: String?): List<String>? {
        return gson.fromJson(episode, episodeToken)
    }
}