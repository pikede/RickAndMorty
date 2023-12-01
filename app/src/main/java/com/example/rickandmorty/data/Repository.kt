package com.example.rickandmorty.data

import com.example.rickandmorty.models.Locations
import com.example.rickandmorty.models.Result
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface Repository {
    suspend fun getCharactersWithName(characterQueryName: String): Flow<ResultResponse<Result>>

    suspend fun getCharacters(): Flow<ResultResponse<Result>>

    suspend fun getCharactersFromNetwork(): ResultResponse<Result>?

    suspend fun getCharactersLocations(id: Int): Response<Locations>

    fun resetPage()
}