package com.example.rickandmorty.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmorty.models.Result
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun addCharacterResult(characterResults: Result)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun addCharacterResults(vararg characterResults: Result)
//
//    @Query("SELECT * FROM RickAndMortyCharacters WHERE name = :name")
//    suspend fun getCharacters(name:String) : Flow<List<Result>>
//
//    @Query("SELECT * FROM RickAndMortyCharacters ORDER BY name")
//    suspend fun getCharacters() : List<Result>
}