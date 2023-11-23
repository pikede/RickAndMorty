package com.example.rickandmorty.data.database

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase

@Database(entities = [Anonymous::class], version = 1, exportSchema = false)
//@TypeConverters(Converter::class)
abstract class AppDataBase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "RickAndMortyDB"
    }

    abstract fun getResultDao(): ResultDao

    abstract fun getLocationsDao(): LocationsDao
}


@Entity(tableName = "RickAndMortyCharacters")

data class Anonymous(
    @PrimaryKey val String: String){}