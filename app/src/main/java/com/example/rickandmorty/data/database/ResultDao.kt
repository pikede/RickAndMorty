package com.example.rickandmorty.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.rickandmorty.models.Location
import com.example.rickandmorty.models.Result
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCharacterResult(characterResult: Result)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCharacterResult(characterResults: List<Result>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCharacterResult(vararg characterResults: Result)

    @Query("SELECT * FROM rickandmortycharacters WHERE name LIKE :name")
    suspend fun getCharacters(name: String): List<Result>

    @Query("SELECT * FROM rickandmortycharacters ORDER BY name")
    suspend fun getCharacters(): List<Result>

    @Query("SELECT * FROM rickandmortycharacters WHERE location in (:locations)")
    fun getCharactersByLocation(locations: List<Location>): Flow<List<Result>>
}