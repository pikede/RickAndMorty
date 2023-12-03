package com.example.rickandmorty.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rickandmorty.data.database.converters.EpisodeConverter
import com.example.rickandmorty.data.database.converters.LocationConverter
import com.example.rickandmorty.data.database.converters.OriginConverter
import com.example.rickandmorty.data.database.converters.ResultConverter
import com.example.rickandmorty.data.database.converters.ResultsConverter
import com.example.rickandmorty.models.Result

@Database(entities = [Result::class], version = 2, exportSchema = false)
@TypeConverters(
    OriginConverter::class,
    LocationConverter::class,
    ResultsConverter::class,
    ResultConverter::class,
    EpisodeConverter::class
)
abstract class AppDataBase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "RickAndMortyDB"
    }

    abstract fun getResultDao(): ResultDao

    abstract fun getLocationsDao(): LocationsDao
}